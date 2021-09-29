/**
 * <h1> Index </h1>
 *
 * Connection to Mongodb and retrieval of reports. The reports are in json format and will
 * be transformed for display
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var cors= require('cors');
const MongoClient = require("mongodb").MongoClient;

// Connection to mongodb
global.mongoURL = "mongodb://localhost:27017/" //Local version
global.ip_address = "http://localhost:4000/";

//var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');
var managerRouter = require('./routes/manager');

MongoClient.connect(mongoURL, { useNewUrlParser: true }, function(err, client) {
    if (err) console.log("Unable to connect to the mongoDB server. Error", err);

    //GET json data from reports
    app.get("/allReports", (request, response, next) => {
        var db = client.db("ires2019");

        db.collection("allReports")
            .find(
                {},
                {
                    projection: {
                        _id: 0,
                        categoryID: 1,
                        latitude: 1,
                        longitude: 1,
                        timestamp: 1,
                        incident: 1,
                        description: 1,
                        severityWeight: 1,
                        confirmedBy: 1,
                        deniedBy: 1
                    }
                }
            )
            .toArray(function(err, result) {
                if (err) throw err;
                response.json(result);
                console.log("\t\t Getting reports ");
                // result.end();
            });
    });
});

// Initialization
const app = express();

// Settings
app.set("view engine", "ejs");
app.set("views", path.join(__dirname, "views"));
// Static files
app.use(express.static(path.join(__dirname, "public")));

app.use(logger('dev'));
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
console.log(path.join(__dirname, 'public'));
app.use(express.static(path.join(__dirname, 'public')));
console.log(path.join(__dirname , '/node_modules'));
app.use(express.static(path.join(__dirname , '/node_modules')));

// Routes -- by default router look for all 'index'
app.use(require("./routes/"));
app.use('/users', usersRouter);
app.use('/manager',managerRouter);

// PORT
const PORT = process.env.PORT || 4000;

// starting the server
app.listen(PORT, () => {
    console.log(`Listening on PORT ${PORT}`);
});
