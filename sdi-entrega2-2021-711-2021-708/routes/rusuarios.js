module.exports = function (app, swig, gestorBD) {

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
                            res.redirect("/identificarse?mensaje=Nuevo usuario registrado");
                            //res.send('Usuario Insertado ' + id);
                        }
                    });
                }
            })

        }
    });

    app.get("/identificarse", function (req, res) {
        let respuesta = swig.renderFile('views/bidentificacion.html', { usuarioSesion: req.session.usuario });
        res.send(respuesta);
    });

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
                if (req.session.usuario.email == "admin@email.es") {
                    res.redirect("/usuario/list");
                } else {
                    res.redirect("/oferta/misofertas");

                }
            }
        });
    });

    app.get('/desconectarse', function (req, res) {
        req.session.usuario = null;
        let respuesta = swig.renderFile('views/bidentificacion.html', {usuarioSesion: null});
        res.send(respuesta);
    });

    //metodo del admin que permite listar usuarios
    app.get('/usuario/list', function (req, res) {
        let usuarioSesion = req.session.usuario;
        let criterio = { rol: 'user' }
        if ( usuarioSesion === null || usuarioSesion.rol === 'user'){
            res.redirect("/identificarse?mensaje=Solo el administrador tiene acceso a esta zona")
        } else {
            gestorBD.obtenerUsuarios(criterio,function (usuarios) {
                let respuesta = swig.renderFile('views/blistadousuariosadmin.html', {
                    usuarios: usuarios,
                    usuarioSesion: usuarioSesion
                });
                res.send(respuesta);
            })
        }

    })

    app.get('/usuario/borrar/:id', function (req, res) {
        gestorBD.borrarUsuario(criterio, function (deleted) {
            let criterio = {"_id": gestorBD.mongo.ObjectID(req.params.id)};
            if (deleted == null)
                res.send("error al borrar");
            else //borramos el usuario, volvemos a cargar la tabla
                gestorBD.obtenerTodosUsuarios(function (usuarios) {
                    if (usuarios == null) {
                        res.status(500);
                        res.json({
                            error: "se ha producido un error al cargar la tabla"
                        })
                    } else {
                        res.status(200);
                        res.send(JSON.stringify(usuarios));
                    }
                })
        })
    });


    app.get("/index", function (req, res) {
        let respuesta = swig.renderFile('views/bindex.html',
            {});
        res.send(respuesta);
    });
}