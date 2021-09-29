var express = require('express');
var router = express.Router();

//Display leaflet map

router.get('/security/index',(req,res)=>{
    res.render('staff_index_security', {  ip_address:global.ip_address});
    res.end();
});

module.exports = router;
