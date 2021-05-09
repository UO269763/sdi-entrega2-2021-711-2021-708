module.exports = function (app, swig, gestorBD) {

    /*
    Un usuario identificado con perfil de Usuario Estándar, debe poder crear una oferta suministrando:
    título descriptivo de la oferta, detalle textual de la oferta, fecha de alta de la oferta(Esta fecha puede ser la
    del sistema) y cantidad solicitada en euros.
     */
    app.get('/oferta/agregar', function (req, res) {
        let respuesta = swig.renderFile('views/bagregar.html', {
            usuarioSesion: req.session.usuario
        });
        res.send(respuesta);
    });

    app.post("/oferta/agregar", function (req, res) {
        if (req.session.usuario == null) {
            res.redirect("/identificarse");
            return;
        } else if (req.body.nombre < 2) {
            res.redirect("/oferta/agregar?mensaje=El nombre de la oferta debe tener mas de 2 caracteres");
            return;
        }
        if (req.body.info === "" || req.body.info === null) {
            res.redirect("/oferta/agregar?mensaje=La información no puede ser vacía");
            return;
        }
        if (req.body.precio === "" || req.body.precio === null) {
            res.redirect("/oferta/agregar?mensaje=El precio no puede ser vacío");
            return;
        }
        if (req.body.precio === "" || req.body.precio <= 0) {
            res.redirect("/oferta/agregar?mensaje=Compruebe el precio de su producto");
            return;
        }
        let now = new Date();
        var oferta = {
            nombre: req.body.nombre,
            info: req.body.info,
            precio: req.body.precio,
            fecha: now.toDateString(),
            autor: req.session.usuario.email,
            estado: 'disponible',
            comprador: undefined
        }
        // Conectarse
        gestorBD.insertarOferta(oferta, function (id) {
            if (id == null) {
                res.send("Error al insertar oferta");
            } else {
                res.redirect("/oferta/misofertas");
            }
        });

    });

    /*
    Un usuario identificado con perfil de Usuario Estándar debe poder acceder a una lista en la que figuren
    todas sus ofertas. Para cada oferta se mostrará: texto descriptivo de la oferta, detalle de la oferta y
    cantidad solicitada en euros.
     */
    app.get("/oferta/misofertas", function (req, res) {
        let usuarioSesion = req.session.usuario;
        console.log(usuarioSesion.money);
        if (usuarioSesion == null) {
            res.redirect("/identificarse");
            return;
        } else {
            let criterio = { autor: req.session.usuario.email };
            gestorBD.obtenerOfertas(criterio, function (ofertas) {
                if (ofertas == null) {
                    res.send("Error al listar");
                } else {
                    let respuesta = swig.renderFile('views/bmisofertas.html',
                        {
                            ofertas: ofertas,
                            usuarioSesion: usuarioSesion
                        });
                    res.send(respuesta);
                }
            });
        }
    });

    /*
    Un usuario identificado con perfil de Usuario Estándar debe poder dar de baja una oferta sobre el listado
    de sus ofertas seleccionado un botón/enlace Eliminar para la oferta que desee suprimir. Al pulsar el
    botón de Eliminar se deben tanto la oferta como toda la información relativa a la misma.
     */
    app.get('/oferta/eliminar/:id', function (req, res) {
        let criterio = {"_id": gestorBD.mongo.ObjectID(req.params.id)};
        gestorBD.eliminarOferta(criterio, function (ofertas) {
            if (ofertas == null) {
                res.send(respuesta);
            } else {
                res.redirect("/oferta/misofertas");
            }
        });
    });

    /*
    Incluir un sistema que permita realizar una búsqueda de ofertas por su título. El cuadro de búsqueda
    contendrá un único campo de texto. La búsqueda debe ser insensible a mayúscula y minúsculas. Por
    ejemplo, si escribimos la cadena “coch” deberá retornar ofertas en los que la cadena “coch”, “COCH”,
    “Coch”, etc. sea parte de su título. Si la cadena es vacía deberá mostrar un listado completo con todas las
    ofertas existentes en el sistema.
     */
    app.get("/oferta/buscar", function (req, res){
        var criterio = {};
        if (req.query.busqueda != null) {
            criterio = {
                $or: [
                    {
                        nombre: {
                            $regex: ".*" + req.query.busqueda + ".*", $options: 'i'
                        }
                    }
                ]
            };
        }
        var pg = parseInt(req.query.pg); //
        if (req.query.pg == null) { // Puede no venir el param
            pg = 1;
        }

        gestorBD.obtenerOfertasPg(criterio, pg, function (ofertas, total) {
            if (ofertas == null) {
                res.send("Error al listar ");
            } else {
                let ultimaPg = total / 4;
                if (total % 4 > 0) {
                    ultimaPg = ultimaPg + 1;
                }
                let paginas = [];
                for (let i = pg - 2; i <= pg + 2; i++) {
                    if (i > 0 && i <= ultimaPg) {
                        paginas.push(i);
                    }
                }
                var respuesta = swig.renderFile('views/bbuscaroferta.html',
                    {
                        usuarioSesion: req.session.usuario,
                        busqueda: req.query.busqueda,
                        ofertas: ofertas,
                        paginas: paginas,
                        ultimaPg: ultimaPg,
                        actual: pg
                    });
                res.send(respuesta);
            }
        });
    });


    /*
    Sobre el listado resultante de una búsqueda de ofertas, un usuario podrá comprar una oferta haciendo
    click en el botón “Comprar” correspondiente. Sólo se permite la compra de una oferta si el Contador de
    dinero del Usuario es igual o superior al precio de la misma.
     */
    app.get('/oferta/comprar/:id', function (req, res) {
        let criterio = {"_id": gestorBD.mongo.ObjectID(req.params.id)};
        console.log(req.session.usuario.email);
        gestorBD.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null) {
                res.send("Error al obtener ofertas");
            } else {
                var precio = ofertas[0].precio;
                var saldo = req.session.usuario.money - precio;
                if (saldo < 0) {
                    res.redirect('/oferta/buscar?mensaje=Sin saldo suficiente');
                } else {
                    var cri = {"_id": gestorBD.mongo.ObjectID(req.session.usuario._id)}
                    var usuario = {
                        nombre: req.session.usuario.nombre,
                        apellidos: req.session.usuario.apellidos,
                        email: req.session.usuario.email,
                        password: req.session.usuario.password,
                        rol: 'user',
                        money: saldo
                    }
                    gestorBD.modificarUsuario(cri, usuario, function (result) {
                        if (result == null) {
                            res.send("Error al modificar usuario");
                        } else {
                            var crit = {"_id": gestorBD.mongo.ObjectID(req.params.id)}
                            var oferta = {
                                nombre: ofertas[0].nombre,
                                info: ofertas[0].info,
                                precio: ofertas[0].precio,
                                fecha: ofertas[0].fecha,
                                autor: ofertas[0].autor,
                                estado: 'no disponible',
                                comprador: req.session.usuario.email
                            }
                            gestorBD.modificarOferta(crit, oferta, function (result) {
                                if (result == null) {
                                    res.send("Error al modificar oferta");
                                } else {
                                    req.session.usuario = usuario;
                                    res.redirect('/oferta/miscompras');
                                }
                            });
                        }
                    });
                }
            }
        })
    });

    app.get("/oferta/miscompras", function (req, res) {
        let criterio = {
            estado: 'no disponible',
            comprador: req.session.usuario.email
        };
        gestorBD.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null) {
                res.send("Error al listar ");
            } else {
                var respuesta = swig.renderFile('views/bmiscompras.html',
                    {
                        ofertas: ofertas,
                        usuarioSesion: req.session.usuario
                    });
                res.send(respuesta);
            }
        });
    });
};
