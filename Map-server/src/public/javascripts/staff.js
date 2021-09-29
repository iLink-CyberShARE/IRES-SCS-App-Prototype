/**
 * <h1> Staff </h1>
 *
 * Creation of table for the management of users
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

let ip_address =document.getElementById("staff-script").getAttribute("ip_address");

function createTable(collection){
    console.log("createTable");
    console.log(collection);
    $.post(ip_address+'users/getUsers?collection='+collection,function(result){
        var table_body = '<h1>Manager Users</h1><hr class="hr1">';
        table_body +=   '<table border="1">';
        table_body += "<tr> <th> User Id</th> <th> Status </th> <th colspan='2'> Change Status </th> </tr>"
        $.each(result,function(i,data){
            console.log(i);
            console.log(data);
            var id = data["_id"];
            var statusString = data["status"]== -1 ? "Pending" : data["status"]== 1 ? "Accepted" : "Declined";
            table_body += '<tr>';
            table_body += '<td>' + data["betaTesterID"] + '</td>';
            table_body += '<td id="status_'+id+'">' + statusString + '</td>';
            table_body += '<td> <button id="'+id+'" type="button" class="button-status accept-status '+collection+'">Accept</button> </td>';
            table_body += '<td> <button id="'+id+'" type="button" class="button-status decline-status '+collection+'">Decline</button> </td>';
            table_body += '</tr>';
            console.log('#status'+id);
            console.log('#Accept_'+id);
            console.log('#Decline_'+id);
        });
        table_body+='</table>';
        $('#'+collection).html(table_body);
    });
    console.log("END createTable");
}

$(document).ready(function(){
    //Nav Bar
    $(".nav-button").click(function(){
        console.log("nav-button");
        $(".nav-button").removeClass("active");
        $(this).addClass("active");
    });

    $(".relation").click(function(){
        var collection = "";
        console.log(".relation");
        console.log(this.id);
        //Make the action orange
        $(".relation").removeClass("select");
        $(this).addClass("select");
        //Make the action orange
        $(".action").removeClass("select");
        $("#manage-box-row2-item1").addClass("select");
    });

    $(".action").click(function(){
        console.log(".action");
        console.log(this.id);
        //Make action orange
        $(".action").removeClass("select");
        $(this).addClass("select");
    });

    $(".button-status").click(function(){
        console.log("--------.Button Status");
        var id = this.id;
        var status = -1;
        var statusString = "";
        var collection = "";
        //Make action orange
        if($(this).hasClass("accept-status")){
            status = 1;
            statusString = "Accepted";

        }else if($(this).hasClass("decline-status")){
            status = 0;
            statusString = "Declined";
        }
        //Shared IRES server for both prototype applications
        if($(this).hasClass("floodingUsers")){
            collection = "floodingUsers";
        }else if($(this).hasClass("securityUsers")){
            collection = "securityUsers";
        }
        console.log(id);
        console.log(statusString);
        console.log(collection);
        $.post(ip_address+'users/updateStatus?id='+id+'&status='+status+"&collection="+collection,function(result){
            var status_id = '#status_'+id
            console.log("Update was done");
            console.log(status_id);
            $(status_id).html(statusString);
        });
    });

    function invisible(){
        console.log("invisible");
        var ids=["#home","#reports","#applications","#manage"];
        for(var i in ids){
            $(ids[i]).addClass("invisible");
        }
    }
});
