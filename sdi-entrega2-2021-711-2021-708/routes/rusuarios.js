module.exports = function(app, swig, gestorBD) {
    app.get("/usuarios", function(req, res) {
        res.send("ver usuarios");
    });

    app.get("/registrarse", function(req, res) {
        let respuesta = swig.renderFile('views/bregistro.html', {});
        res.send(respuesta);
    });

    app.post('/usuario', function(req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        let usuario = {
            email : req.body.email,
            nombre: req.body.nombre,
            apellidos: req.body.apellidos,
            password : seguro
        }
        gestorBD.insertarUsuario(usuario, function(id) {
            if (id == null){
                // res.send("Error al insertar el usuario");
                res.redirect("/registrarse?mensaje=Error al registrar al usuario");
            } else {
                res.redirect("/identificarse?mensaje=Nuevo usuario registrado");
                //res.send('Usuario Insertado ' + id);
            }
        });
    });

    app.get("/identificarse", function(req, res) {
        let respuesta = swig.renderFile('views/bidentificacion.html', {});
        res.send(respuesta);
    });

    app.post("/identificarse", function(req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        let criterio = {
            email : req.body.email,
            password : seguro
        }
        gestorBD.obtenerUsuarios(criterio, function(usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                req.session.usuario = null;
                // res.send("No identificado: ");
                res.redirect("/identificarse" +
                    "?mensaje=Email o password incorrecto"+
                    "&tipoMensaje=alert-danger ");
            } else {
                req.session.usuario = usuarios[0].email;
                if (req.session.usuario == "admin@email.es"){
                    res.redirect("/usuarios/list");
                } else {
                    res.redirect("/publicaciones");
                }
            }
        });
    });

    app.get('/desconectarse', function (req, res) {
        req.session.usuario = null;
        res.send("Usuario desconectado");
    });

    //metodo del admin que permite listar usuarios
    app.get('/usuarios/list', function(req, res) {
        gestorBD.obtenerTodosUsuarios(function (usuarios) {
            let respuesta = swig.renderFile('views/blistadousuariosadmin.html', {
                usuarios: usuarios
            });
            res.send(respuesta);
        })
    })

    app.get('/usuario/borrar/:id', function(req, res) {
        let criterio = {"_id": gestorBD.mongo.ObjectID(req.params.id)};
        gestorBD.borrarUsuario(criterio, function (deleted){
            if (deleted==null)
                res.send("error al borrar");

        })
    })
}