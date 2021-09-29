 /**
  * <h1> Index </h1>
  *
  * Connection to the mongodb database, retrieval of database information and update of parameters
  *
  *
  * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
  * Smart Cities investigators and contributing participants.
  */

// Import package
var mongodb = require('mongodb');
var ObjectID = mongodb.ObjectID;
var express = require('express');
var bodyParser = require('body-parser');
var multer = require('multer');
fileType = require('file-type');
var fs = require('fs');

//Create Express Service
var app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

//Create MongoDB Client
var MongoClient = mongodb.MongoClient;
var uri = "mongodb://localhost:27017/" //Local version

var destinationAppPics = "./public/uploads/";

// PORT
const PORT = process.env.PORT || 3000;

// Add CORS and allowed headers
app.use((req, res, next) => {
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader(
    "Access-Control-Allow-Headers",
    "Origin, X-Requested-With, Content-Type, Accept"
  );
  res.setHeader(
    "Access-Control-Allow-Methods",
    "GET, POST, OPTIONS"
  );
  next();
});

//Connect to MongoDB
MongoClient.connect(uri, {useNewUrlParser: true}, function(err, client){
    if (err)
        console.log('Unable to connect to the mongoDB server. Error', err);
    else{
        app.post('/alert', (request,response, next)=>{
          var post_data = request.body;
          var latitude = post_data.latitude;
          var longitude = post_data.longitude;
          var timestamp = post_data.timestamp;

          var insertJson = {
            'latitude': latitude,
            'longitude':longitude,
            'timestamp': timestamp,
        };
        var db = client.db('ires2019');

          // Insert data
          db.collection('alerts')
          .insertOne(insertJson, function(error, res){
              response.json('Alert Sent');
              console.log('Alert Sent'); 
          })
        });

         // Infrastructure
         app.post('/createReport', (request,response, next)=>{
          var insertJson = formJson(request);

          var db = client.db('ires2019');
            // Insert data
            db.collection('allReports')
            .insertOne(insertJson, function(error, res){
                response.json('Report Sent to allReports');
                console.log('Report Sent to allReports');
            })
          });

         // This method updates the confirm field of reports.
         app.put('/updateConfirm/:id', (request, response, next) => {
             var post_data = request.body;
             var db = client.db('ires2019');
             var id = request.params.id;
             var userID = post_data.userID;
             id = ObjectID(id);
             var query = {_id: id};
             var newvalues = {$inc:{confirmedBy:1}, $push:{usersConfirmed: userID}};
             db.collection("allReports").findOneAndUpdate(
                 query,
                 newvalues,
                 { returnOriginal: false },
                 function (err, documents) {
                     response.header('Content-Type', 'application/json');
                     response.send(documents);
                     response.end();
                 }
             );
         })

        // This method updates the deny field of the report.
        app.put('/updateDeny/:id', (request, response, next) => {
            var post_data = request.body;
            var db = client.db('ires2019');
            var id = request.params.id;
            var userID = post_data.userID;
            id = ObjectID(id);
            var query = {_id: id};
            var newvalues = {$inc:{deniedBy:1}, $push:{usersDenied: userID}};
            console.log(userID);
            db.collection("allReports").findOneAndUpdate(
                query,
                newvalues,
                { returnOriginal: false },
                function (err, documents) {
                    response.header('Content-Type', 'application/json');
                    response.send(documents);
                    response.end();
                }
            );
        })

          function formJson(request){
            var post_data = request.body;
              var categoryID = parseInt(post_data.categoryID);
              var longitude = post_data.longitude;
              var latitude = post_data.latitude;
              var timestamp = post_data.timestamp;
              var incident = parseInt(post_data.incident);
              var description = post_data.description;
              var imageName = post_data.imageName;
              var userID = post_data.userID.toString();
              console.log(userID);
              var hasConfirmed = post_data.hasConfirmed;
              var hasDenied = post_data.hasDenied;
              var confirmedBy = parseInt(post_data.confirmedBy);
              var deniedBy = parseInt(post_data.deniedBy);
              var severityWeight = post_data.severityWeight;
              var reportID = ObjectID(request.params.id).toString();
              console.log(post_data);

              var json = {
                'categoryID': categoryID,
                'latitude': latitude,
                'longitude':longitude,
                'timestamp':timestamp,
                'incident': incident,
                'description': description,
                  'imageName':imageName,
                  'userID': userID,
                  'hasConfirmed': hasConfirmed,
                  'hasDenied': hasDenied,
                  'confirmedBy': confirmedBy,
                  'deniedBy': deniedBy,
                  'severityWeight': severityWeight,
                  'reportID' : reportID,
                  'usersConfirmed': [],
                  'usersDenied': []
              };
            return json;
          }

        // GET json data from reports
        app.get('/reports', (request,response,next)=>{
            var categoryID = parseInt(request.query.categoryID);

            // Database name
            var db = client.db('ires2019');

            
            // Query reports
            var query = (categoryID == -1) ? { } : {"categoryID" : categoryID};
            db.collection('allReports').find(query).sort({_id:-1}).limit(50).toArray(function(err, result){
              if (err) throw err;
              response.json(result);
              console.log(result);
            })
        });



        // GET json data from panic button 
        app.get('/location', (request, response, next)=>{
            var get_data=request.body;
            var db = client.db('ires2019');

            db.collection('alerts').find({}, { projection: {_id: 0, latitud: 1, longitud: 1 } }).toArray(function(err, result) {
                if (err) throw err;
                response.json('coordinates exist');
                console.log(result);            
            })

        });

        app.post('/upload', (req, res) => {
            upload(req, res, function (err) {
                if (err) {
                    console.log(err.message);
                    res.status(400).json({message: err.message});
                } else {
                    console.log(req.file.filename);
                    let path = destinationAppPics + req.file.filename;
                    res.status(200).json({message: 'Image Uploaded Successfully !', path: path});
                }
            })
        });


        app.get('/download/:imagename', (req, res) => {

            let imagename = req.params.imagename;
            let imagepath = destinationAppPics + imagename;
            let image = fs.readFileSync(imagepath);
            let mime = fileType(image).mime;

            res.writeHead(200, {'Content-Type': mime });
            res.end(image, 'binary');
        });

        // Start Web Server 3000 is the port where it is connected to the server
        app.listen(PORT,()=>{
            console.log(`Connected to MongoDB Server, WebService running on port ${PORT}`);  
        })
    }
})

var storage = multer.diskStorage(
    {
        destination: destinationAppPics,
        filename: function ( req, file, cb ) {
            console.log(destinationAppPics);
            cb( null, file.originalname);
        }
    }
);

const upload = multer({
    destination: destinationAppPics,
    limits: {fileSize: 10 * 1024 * 1024, files: 1},
    storage:storage,
    fileFilter:  (req, file, callback) => {

        if (!file.originalname.match(/\.(jpg|jpeg|png)$/)) {

            return callback(new Error('Only Images are allowed !'), false);
        }

        callback(null, true);
    }
}).single('file');
