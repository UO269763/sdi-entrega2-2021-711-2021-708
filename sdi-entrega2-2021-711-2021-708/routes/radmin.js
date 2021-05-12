module.exports = function (app, swig, gestorBD) {

    // Vamos a la vista de administrador
    app.get("/admin", function (req, res) {
        let respuesta = swig.renderFile('views/badmin.html', {
            usuarioSesion: req.session.usuario
        });
        res.send(respuesta);
    });

    // Solamente el admin tendra acceso a resetear
    app.get("/resetdb", function (req, res) {
        let usuarioSesion = req.session.usuario;
        if ( usuarioSesion === null || usuarioSesion.rol === 'user'){
            res.redirect("/identificarse?mensaje=Solo el administrador tiene acceso a esta zona")
        } else {
            gestorBD.resetDB(function (result) {
                if (result == null) {
                    res.redirect("/identificarse?mensaje=Error al resetear las colecciones&tipoMensaje=alert-danger");
                } else {
                    addAdmin(res);
                }
            });
        }

    });

    function addAdmin(res) {
        let seguro = app.get("crypto").createHmac('sha256',
            app.get('clave')).update('admin').digest('hex');
        let cri = {email: 'admin@email.com'};
        gestorBD.obtenerUsuarios(cri, function (usuarios) {
            if (usuarios != null && usuarios.length !== 0) {
                res.redirect("/identificarse?mensaje=Admin ya existe" + "&tipoMensaje=alert-danger ");
            } else {
                let admin = {
                    nombre: 'admin',
                    apellidos: 'admin',
                    email: 'admin@email.com',
                    password: seguro,
                    rol: 'admin',
                    money: 100.0
                };
                gestorBD.insertarUsuario(admin, function (id) {
                    if (id == null) {
                        res.redirect("/identificarse?mensaje=Error al insertar admin en reset&tipoMensaje=alert-danger");
                    } else {
                        createData(res);
                    }
                })
            }
        })
    }

    function createData(res) {
        const listaUsuarios = [];
        const max = 10;
        const numOffersUser = 5;
        const numMessageUser = 2;
        for (let i = 1; i <= max; i++) {
            let password = app.get("crypto").createHmac('sha256',
                app.get('clave')).update('user' + i).digest('hex');
            let user = {
                nombre: 'user' + i,
                apellidos: 'user' + i,
                email: 'user' + i + '@email.com',
                password: password,
                rol: 'user',
                money: 100.0            };
            listaUsuarios.push(user);
        }
        gestorBD.insertDataTest(listaUsuarios, 'usuarios', function (users) {
            if (users == null || users.length === 0) {
                res.redirect("/identificarse?mensaje=Error al crear los usuarios de prueba");
            } else {
                const listaOfertas = [];
                listaUsuarios.forEach(user => {
                    for (let i = 1; i <= numOffersUser; i++) {
                        let oferta = {
                            nombre: 'Oferta ' + i + ' del usuario ' + user.nombre,
                            info: 'Info de la oferta ' + i + ' del usuario ' + user.nombre,
                            precio: i * 5,
                            autor: user.email,
                            estado: 'disponible',
                            comprador: null
                        };
                        listaOfertas.push(oferta);
                    }
                });
                gestorBD.insertDataTest(listaOfertas, 'ofertas', function (ofertas) {
                    if (ofertas == null || ofertas.length === 0) {
                        res.redirect("/identificarse?mensaje=Error al crear las ofertas de prueba");
                    } else {
                        const listMessages = [];
                        listaOfertas.forEach(oferta => {
                            listaUsuarios.forEach(usuario => {
                                if (usuario.email !== oferta.autor) {
                                    for (let i = 1; i <= numMessageUser; i++) {
                                        let message = {
                                            sender: usuario.email,
                                            receiver: oferta.autor,
                                            offer: oferta._id,
                                            message: 'Mensaje ' + i + 'del usuario ' + usuario.nombre,
                                            fecha: new Date().toDateString(),
                                            read: false
                                        };
                                        listMessages.push(message);
                                    }
                                }
                            });
                        });

                    }
                });
            }
        });
    }
};