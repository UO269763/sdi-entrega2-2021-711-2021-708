module.exports = function (app, swig, gestorBD) {

    // Nos redirige a la vista para registrar un usuario
    app.get("/registrarse", function (req, res) {
        let respuesta = swig.renderFile('views/bregistro.html', { usuarioSesion: req.session.usuario});
        res.send(respuesta);
    });

    /*
    Los usuarios deben poder registrarse en la aplicación aportando email, nombre, apellidos y una
    contraseña (que deberá repetirse dos veces y coincidir entre sí).
    */
    app.post('/registrarse', function (req, res) {
        if (req.body.nombre.length < 2) {
            res.redirect("/registrarse?mensaje=El nombre debe tener mas de 2 caracteres");
            return;
        } else if (req.body.email === "" || req.body.email == null) {
            res.redirect("/registrarse?mensaje=El email no puede estar vacío");
            return;
        } else if (!req.body.email.includes("@")) {
            res.redirect("/registrarse?mensaje=El email debe contener @");
            return;
        } else if (req.body.password.length < 4) {
            res.redirect("/registrarse?mensaje=La contraseña debe tener 4 o más caracteres");
            return;
        } else if (req.body.apellidos.length < 2) {
            res.redirect("/registrarse?mensaje=El apellido debe tener mas de 2 caracteres");
            return;
        } else if (req.body.password !== req.body.rpassword) {
            res.redirect("/registrarse?mensaje=Las contraseñas no coinciden.");
            return;
        } else {
            let seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
                .update(req.body.password).digest('hex');
            let usuarioemail = {
                email: req.body.email
            }
            gestorBD.obtenerUsuarios(usuarioemail, function (usuarios){
                if (usuarios != null && usuarios.length !== 0){
                    res.redirect("/registrarse?mensaje=Este email ya está registrado. Intentelo de nuevo");
                } else  {
                    let usuario = {
                        email: req.body.email,
                        nombre: req.body.nombre,
                        apellidos: req.body.apellidos,
                        password: seguro,
                        rol: 'user',
                        money: 100.0
                    };
                    gestorBD.insertarUsuario(usuario, function (id) {

                        if (id == null) {
                            // res.send("Error al insertar el usuario");
                            res.redirect("/registrarse?mensaje=Error al registrar al usuario");
                        } else {
                            req.session.usuario = usuario;
                            res.redirect("/oferta/buscar?mensaje=Nuevo usuario registrado");
                        }
                    });
                }
            })

        }
    });

    // Nos redirige a la vista para iniciar sesion un usuario
    app.get("/identificarse", function (req, res) {
        let respuesta = swig.renderFile('views/bidentificacion.html', { usuarioSesion: req.session.usuario });
        res.send(respuesta);
    });

    // Comprueba que los datos introducidos coincidan con un usario, sino lanzo error
    app.post("/identificarse", function (req, res) {
        if (req.body.email === "" || req.body.email == null) {
            res.redirect("/registrarse?mensaje=El email no puede estar vacío");
            return;
        } else if (!req.body.email.includes("@")) {
            res.redirect("/registrarse?mensaje=El email debe contener @");
            return;
        }
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        let criterio = {
            email: req.body.email,
            password: seguro
        }
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                req.session.usuario = null;
                // res.send("No identificado: ");
                res.redirect("/identificarse" +
                    "?mensaje=Email o password incorrecto" +
                    "&tipoMensaje=alert-danger ");
            } else {
                req.session.usuario = usuarios[0];
                if (req.session.usuario.rol == "admin") {
                    res.redirect("/usuario/list");
                } else {
                    res.redirect("/oferta/buscar");

                }
            }
        });
    });

    // Nos desconecta de la sesion
    app.get('/desconectarse', function (req, res) {
        req.session.usuario = null;
        let respuesta = swig.renderFile('views/bidentificacion.html', {usuarioSesion: null});
        res.send(respuesta);
    });

    //metodo del admin que permite listar usuarios
    app.get('/usuario/list', function (req, res) {
        let usuarioSesion = req.session.usuario;
        if ( usuarioSesion === null || usuarioSesion.rol === 'user'){
            res.redirect("/identificarse?mensaje=Solo el administrador tiene acceso a esta zona")
        } else {
            let criterio = { rol: 'user' }
            gestorBD.obtenerUsuarios(criterio,function (usuarios) {
                if (usuarios==null){
                    res.redirect("/index?mensaje=Error al listar usuarios");
                } else {
                    let respuesta = swig.renderFile('views/blistadousuariosadmin.html', {
                        usuarios: usuarios,
                        usuarioSesion: usuarioSesion
                    });
                    res.send(respuesta);
                }

            })
        }

    })

    //metodo del admin que permite borrar usuarios
    app.post("/usuario/borrar", function (req,res){
        let idsUsuarios = req.body.checkbox;
        if (idsUsuarios === undefined){
            res.redirect("/usuario/list?mensaje=Los usuarios no se pudieron eliminar");
        } else {
            if (!Array.isArray(idsUsuarios)) {
                let aux = idsUsuarios;
                idsUsuarios = [];
                idsUsuarios.push(aux);
            }
            // borramos conversaciones
            let criterioConversacion = {
                $or: [{user1: {$in: idsUsuarios}},
                    {user2: {$in: idsUsuarios}}]
            };
            gestorBD.eliminarConversaciones(criterioConversacion, function (conversaciones){
                if (conversaciones == null){
                    res.redirect("/usuario/list" +
                        "?mensaje=No se pudieron borrar las conversaciones de los usuarios");
                } else {
                    // pasamos a borrar los mensajes
                    let criteriomensajes = {
                        $or: [{receiver: {$in: idsUsuarios}},
                            {sender: {$in: idsUsuarios}}]
                    };
                    gestorBD.eliminarMensajes(criteriomensajes, function(mensajes){
                        if (conversaciones == null){
                            res.redirect("/usuario/list" +
                                "?mensaje=No se pudieron borrar los mensajes de los usuarios");
                        } else {
                            //borramos ofertas
                            let criterioOferta = {
                                autor: {$in: idsUsuarios}
                            };
                            gestorBD.eliminarOferta(criterioOferta, function (ofertas){
                                if (ofertas==null){
                                    res.redirect("/usuario/list" +
                                        "?mensaje=No se pudieron borrar las ofertas del usuario")
                                } else {
                                    //borramos el usuario
                                    let criterio = {
                                        email: {$in: idsUsuarios}
                                    };
                                    gestorBD.borrarUsuario(criterio, function (usuarios){
                                        if (usuarios == null || usuarios.length === 0) {
                                            res.redirect("/usuario/list" +
                                                "?mensaje=Los usuarios no pudieron eliminarse");
                                        } else {
                                            res.redirect("/usuario/list" +
                                                "?mensaje=Los usuarios se eliminaron correctamente");
                                        }
                                    })
                                }
                            })
                        }
                    })
                }
            })
        }
    });

    app.get("/index", function (req, res) {
        let respuesta = swig.renderFile('views/bindex.html',
            {});
        res.send(respuesta);
    });
}