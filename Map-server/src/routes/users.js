/**
 * <h1> Users </h1>
 *
 * Registration of users into the database
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

var express = require('express');
var crypto = require('crypto');
var router = express.Router();
const shortid = require('shortid');

//Connection to MongoDB
var mongodb=require('mongodb');
var ObjectID = require('mongodb').ObjectID;

var MongoClient = mongodb.MongoClient;
var db_name = 'ires2019'
//Password utils
//Create function to random salt
var genRandomString = function (length){
  return crypto.randomBytes(Math.ceil(length/2)).toString('hex').slice(0,length);
}

var sha512 = function(password,salt){
  var hash = crypto.createHmac('sha512',salt);
  hash.update(password);
  var value = hash.digest('hex');
  return {
    salt:salt,
    passwordHash:value
  };
}

function saltHashPassword(userPassword){
  var salt = genRandomString(16);
  var passwordData = sha512(userPassword,salt);
  return passwordData;
}

function checkHashPassword(userPassword,salt){
    var passwordData = sha512(userPassword,salt);
    return passwordData;
}

//Register user into the database
router.post('/register',(req,res,next)=>{
  MongoClient.connect(global.mongoURL,{useNewUrlParser:true} ,function(err, client) {
    if (err) {
      console.log("Unable to connect to MongoDB",err);
    }else {
      var post_data = req.body;

      var collection = post_data.collection;
      var betaTesterID = post_data.betaTesterID;
      var status = post_data.status;
        var insertJson = {
        'betaTesterID': betaTesterID,
        'status': status
        };

      console.log(insertJson);
      var db=client.db(db_name);
      // Reports or Test collections
      db.collection(collection).find({'betaTesterID':betaTesterID}).count(function(err,number){
          var result="false";
          var msg="";
          if(number !=0){
              msg='Student Id already exists';
              console.log('Student Id already exists');
              var json={
                  'message':msg,
                  'result':result
              };
              console.log(json);
              res.setHeader('Content-Type', 'application/json');
              res.send(json);
              res.end();
              client.close();
          }else{
              db.collection(collection).insertOne(insertJson,function(error,res2){
                  msg = 'Registration success';
                  result="true";
                  console.log('Registration success');
                  var json={
                      'message':msg,
                      'result':result
                  };
                  console.log(json);
                  res.setHeader('Content-Type', 'application/json');
                  res.send(json);
                  res.end();
                  client.close();
              })
          }
      });
    }
  });
});

// Register user into the database
router.post('/login',(req,res,next)=>{
    MongoClient.connect(global.mongoURL,{useNewUrlParser:true} ,function(err, client) {
    if (err) {
      console.log("Unable to connect to MongoDB",err);
    }else {
        console.log('login');
        var post_data = req.body;
        console.log(post_data)
        var betaTesterID = post_data.betaTesterID;
        var collection = post_data.collection;
        console.log(collection);
        console.log(betaTesterID);

        var db=client.db(db_name);
        // Reports or Test collections
        db.collection(collection).find({'betaTesterID':betaTesterID}).count(function(err,number){
            var result = "0";
            var msg="";
            if(number ==0){
              result = "-2";
              msg='Student Id not exists';
              console.log(msg);
              var json={
                'message':msg,
                'result':result
              };
              console.log(json);
              res.setHeader('Content-Type', 'application/json');
              res.send(json);
              res.end();
              client.close();
            }else{
              console.log(betaTesterID);
              db.collection(collection).findOne({'betaTesterID':betaTesterID},function(error,user){
                var status = user.status;
                if(status==1){
                    result = "1";
                    msg='Login success';
                }else if(status==0){
                    result = "0";
                    msg='Permission denied';
                }else{
                    result = "-1";
                    msg='Permission pending';
                }
                var json={
                  'message':msg,
                  'result':result
                };
                console.log(json);
                res.setHeader('Content-Type', 'application/json');
                res.send(json);
                res.end();
                client.close();
              })
            }

          });
    }
  });
});

// Register user into the database
router.post('/getUsers',(req,res,next)=>{
    var collection = req.query.collection;
    console.log(collection);
    MongoClient.connect(global.mongoURL, {useNewUrlParser: true}, function (err, client) {
        if (err) {
            console.log("Unable to connect to MongoDB", err);
        } else {
            var db = client.db(db_name);
            db.collection(collection).find({}).toArray(function(err,result){
                if(err) throw err;
                console.log("Documents retrieved");
                res.setHeader('Content-Type', 'application/json');
                res.send(result);
                res.end();
                client.close();
            });
        }
    });
});

// Register user into the database
router.post('/updateStatus',(req,res,next)=>{
    var id = req.query.id;
    var status = req.query.status;
    var collection = req.query.collection;
    console.log("Id= "+id);
    console.log("New status= "+status);
    MongoClient.connect(global.mongoURL, {useNewUrlParser: true}, function (err, client) {
        if (err) {
            console.log("Unable to connect to MongoDB", err);
        } else {
            var db = client.db(db_name);
            var myquery = { _id: ObjectID(id) };
            var newvalues = { $set: { status: status } };
            db.collection(collection).updateOne(myquery, newvalues, function(err, result) {
                if (err) throw err;
                console.log("1 document updated");
                res.sendStatus(200);
                res.end();
                client.close();
            });
        }
    });
});

router.get('/generateRandomIDs',(req,res,next)=>{
    var json=[];
    var jsonBuffer;
    var lim=parseInt(req.query.limit);
    var collection=req.query.collection;
    console.log(lim)
    console.log(collection);
    var betaTesterID = 0;
    console.log(global.mongoURL);
    for(var i=0;i<lim;i++) {
        console.log("Hello");

        betaTesterID = shortid.generate().substring(0,5);
        jsonBuffer = {
            'status': "1",
            'betaTesterID': betaTesterID,
        };
        json.push(jsonBuffer);
    }
    console.log(json);
    console.log("--");

    MongoClient.connect(global.mongoURL,{useNewUrlParser:true} ,function(err, client) {
        if (err) {
            console.log("Unable to connect to MongoDB",err);
        }else{
            db=client.db(db_name);
            db.collection(collection).insertMany(json, function(err, result) {
                if (err) throw err;
                console.log(result["ops"]);
            });
        }
        client.close();
        res.end()
    });
});

module.exports = router;
