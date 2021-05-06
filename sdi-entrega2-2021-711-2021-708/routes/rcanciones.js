module.exports = function(app, swig, gestorBD) {


    app.get("/ofertas", function(req, res) {
        let canciones = [ {
            "nombre" : "Blank space",
            "precio" : "1.2"
        }, {
            "nombre" : "See you again",
            "precio" : "1.3"
        }, {
            "nombre" : "Uptown Funk",
            "precio" : "1.1"
        } ];

        let respuesta = swig.renderFile('views/btienda.html', {
            vendedor : 'Tienda de canciones',
            canciones : canciones
        });

        res.send(respuesta);
    });

    app.get('/ofertas/agregar', function (req, res) {
        if ( req.session.usuario == null){
            res.redirect("/tienda");
            return;
        }
        let respuesta = swig.renderFile('views/bagregar.html', {

        });
        res.send(respuesta);
    });

    app.get('/canciones/:genero/:id', function(req, res) {
        let respuesta = 'id: ' + req.params.id + '<br>'
            + 'Género: ' + req.params.genero;
        res.send(respuesta);
    });

    app.get('/suma', function(req, res) {
        let respuesta = parseInt(req.query.num1) + parseInt(req.query.num2);
        res.send(String(respuesta));
    });

    app.post("/oferta", function (req, res) {
        if ( req.session.usuario == null){
            res.redirect("/tienda");
            return;
        }

        var oferta = {
            nombre : req.body.nombre,
            info : req.body.info,
            precio : req.body.precio,
            fecha : req.body.fecha,
            autor: req.session.usuario
        }
        // Conectarse
        gestorBD.insertarOferta(oferta, function(id){
            if (id == null) {
                res.send("Error al insertar oferta");
            } else {
                res.redirect("/ofertas");
            }
        });

    });

    app.get('/promo*', function (req, res) {
        res.send('Respuesta patrón promo* ');
    });

    app.get("/tienda", function(req, res) {
        let criterio = {};
        if( req.query.busqueda != null ){
            criterio = { "nombre" : {$regex : ".*"+req.query.busqueda+".*"} };
        }

        let pg = parseInt(req.query.pg); // Es String !!!
        if ( req.query.pg == null){ // Puede no venir el param
            pg = 1;
        }

        gestorBD.obtenerOfertasPg(criterio, pg , function(ofertas, total ) {
            if (ofertas == null) {
                res.send("Error al listar ");
            } else {
                let ultimaPg = total/4;
                if (total % 4 > 0 ){ // Sobran decimales
                    ultimaPg = ultimaPg+1;
                }
                let paginas = []; // paginas mostrar
                for(let i = pg-2 ; i <= pg+2 ; i++){
                    if ( i > 0 && i <= ultimaPg){
                        paginas.push(i);
                    }
                }
                let respuesta = swig.renderFile('views/btienda.html',
                    {
                        ofertas : ofertas,
                        paginas : paginas,
                        actual : pg
                    });
                res.send(respuesta);
            }
        });
    });


    app.get('/oferta/:id', function (req, res) {
        let criterio = { "_id" : gestorBD.mongo.ObjectID(req.params.id) };
        gestorBD.obtenerOfertas(criterio,function(ofertas){
            if ( canciones == null ){
                res.send("Error al recuperar la oferta.");
            } else {
                let respuesta = swig.renderFile('views/bcancion.html',
                    {
                        oferta: ofertas[0]
                    });
                res.send(respuesta);
            }
        });
    });

    app.get("/publicaciones", function(req, res) {
        let criterio = { autor : req.session.usuario };
        gestorBD.obtenerOfertas(criterio, function(ofertas) {
            if (ofertas == null) {
                res.send("Error al listar ");
            } else {
                let respuesta = swig.renderFile('views/bpublicaciones.html',
                    {
                        ofertas : ofertas
                    });
                res.send(respuesta);
            }
        });
    });



    app.get('/oferta/eliminar/:id', function (req, res) {
        let criterio = {"_id" : gestorBD.mongo.ObjectID(req.params.id) };
        gestorBD.eliminarOferta(criterio,function(ofertas){
            if ( ofertas == null ){
                res.send(respuesta);
            } else {
                res.redirect("/publicaciones");
            }
        });
    });

    app.get('/cancion/comprar/:id', function (req, res) {
        let cancionId = gestorBD.mongo.ObjectID(req.params.id);
        let compra = {
            usuario : req.session.usuario,
            cancionId : cancionId
        }
        gestorBD.insertarCompra(compra ,function(idCompra){
            if ( idCompra == null ){
                res.send(respuesta);
            } else {
                let idURL =  { "_id" : gestorBD.mongo.ObjectID(req.params.id) };
                gestorBD.obtenerCanciones(idURL, function (canciones) {
                    if(canciones!=null) {
                        if(canciones[0].autor == req.session.usuario)
                            res.send("error");
                        else
                            res.redirect("/compras");
                    }
                    else {
                        res.send(respuesta);
                    }
                })
            }
        });
    });

    app.get('/compras', function (req,res) {
        let criterio = { "usuario" : req.session.usuario};

        gestorBD.obtenerCompras(criterio, function(compras){
            if(compras == null){
                res.send("Error al listar");
            }else {
                let cancionesCompradasIds = [];

                for(let i=0; i < compras.length;i++){
                    cancionesCompradasIds.push(compras[i].cancionId);
                }

                let criterio = {"_id" : {$in: cancionesCompradasIds} }
                gestorBD.obtenerCanciones(criterio, function(canciones){
                    let respuesta = swig.renderFile('views/bcompras.html', {
                        canciones : canciones
                    });
                    res.send(respuesta);
                });
            }
        });
    });


    //Listado de ofertas
    app.get("/listarOfertas", function (req, res) {
        //Para comprobar si el usuario está en sesion
        let usuarioSesion = req.session.usuario;
        if (usuarioSesion == null) {
            res.redirect("/identificarse");
            return;
        } else {
            //Búsqueda  por nombre.
            let criterio = {};
            if (req.query.busqueda != null) {
                criterio = {
                    $or: [
                        { "nombre": { $regex: ".*" + req.query.busqueda + ".*" } }
                    ]
                };
            }

            //Paginacion
            let pg = parseInt(req.query.pg);
            if (req.query.pg == null) { // Puede no venir el param
                pg = 1;
            }

            gestorBD.obtenerListadoOfertasPg(criterio, pg, function (ofertas, total) {
                if (ofertas == null) {
                    res.send("Error al listar ");
                } else {
                    let ultimaPg = total / 5;
                    if (total % 5 > 0) { // Sobran decimales
                        ultimaPg = ultimaPg + 1;
                    }
                    let paginas = []; // paginas mostrar
                    for (let i = pg - 2; i <= pg + 2; i++) {
                        if (i > 0 && i <= ultimaPg) {
                            paginas.push(i);
                        }
                    }

                    let respuesta = swig.renderFile('views/bListarOfertas.html',
                        {
                            ofertas: ofertas,
                            paginas: paginas,
                            actual: pg,
                            usuarioSesion: usuarioSesion
                        });
                    res.send(respuesta);
                }
            });
        }
    });



};
