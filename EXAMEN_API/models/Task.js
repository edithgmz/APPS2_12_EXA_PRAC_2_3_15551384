var db=require('../dbconnection'); //reference of dbconnection.js
 
var Task={ 
    getAllTasks:function(callback){ 
        return db.query("Select * from clima",callback);
    },
    getTaskById:function(id,callback){ 
        return db.query("select * from clima where id=?",[id],callback);
    },
    addTask:function(Task,callback){
        return db.query("insert into clima(temperatura,humedad,hora,fecha) values(?,?,?,?)",[Task.Temperatura,Task.Humedad,Task.Hora,Task.Fecha],callback);
    },
    deleteTask:function(id,callback){
        return db.query("delete from clima where id=?",[id],callback);
    },
    updateTask:function(id,Task,callback){
        return db.query("update clima set temperatura=?, humedad=? where id=?", [Task.Temperatura, Task.Humedad,id],callback);
    }  
};

module.exports=Task;