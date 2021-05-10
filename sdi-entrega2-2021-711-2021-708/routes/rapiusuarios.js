module.exports = function(app, gestorBD) {

    app.post("/api/identificarse", function(req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');

        let criterio = {
            email : req.body.email,
            password : seguro
        };

        gestorBD.obtenerUsuarios(criterio, function(usuarios) {
            if(usuarios == null || usuarios.length == 0) {
                res.status(401);
                app.get("logger").info('API: Error al obtener los usuarios');
                res.json({
                    autenticado : false
                })
            } else {
                var token = app.get('jwt').sign(
                    {usuario: criterio.email , tiempo: Date.now()/1000},
                    "secreto");

                res.status(200);
                app.get("logger").info('API: Usuario autenticado correctamente');
                res.json({
                    autenticado : true,
                    email : criterio.email,
                    token : token
                })
            }
        });
    });


}