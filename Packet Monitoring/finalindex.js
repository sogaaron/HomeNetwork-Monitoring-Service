//google_mqtt
var fs = require('fs'); 
var jwt = require('jsonwebtoken'); 
var mqtt = require('mqtt'); 

var projectId = 'packet-monitoring-system'; 
var cloudRegion = 'us-central1'; 
var registryId = 'Wireless-Nodes'; 
var deviceId = 'Wireless-Node'; 
 
var mqttHost = 'mqtt.googleapis.com'; 
var mqttPort = 8883; 
var privateKeyFile = './certs/rsa_private.pem'; 
var algorithm = 'RS256'; 
var messageType = 'state'; // or event 
 
var mqttClientId = 'projects/' + projectId + '/locations/' + cloudRegion + '/registries/' + registryId + '/devices/' + deviceId; 
var mqttTopic = '/devices/' + deviceId + '/' + messageType; 
var mqttTopic2 = '/devices/' + deviceId + '/events/aaa';

var iatTime = null;   //???
 
var connectionArgs = { 
  host: mqttHost, 
  port: mqttPort, 
  clientId: mqttClientId, 
  username: 'unused', 
  password: createJwt(projectId, privateKeyFile, algorithm), 
  protocol: 'mqtts', 
  secureProtocol: 'TLSv1_2_method' 
}; 

function sleep (delay) {
   var start = new Date().getTime();
   while (new Date().getTime() < start + delay);
}



//firestore
const admin = require('firebase-admin');
let serviceAccount = require('./packet-monitoring-system-9b47182a8da7.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

let db = admin.firestore();
/*
let deviceRef = db.collection('gabi');
let query = deviceRef.where('mac').get()
  .then(snapshot => {
    if (snapshot.empty) {
      console.log('No matching documents.');
      return;
    }

    snapshot.forEach(doc => {
      console.log(doc.id, '=>', doc.data());
    });
  })
  .catch(err => {
    console.log('Error getting documents', err);
  });
*/

//var addsocket = null;

db.collection('gabriel').get()		// When running this program first
  .then((snapshot) => {
    snapshot.forEach((doc) => {
	var addsocket = getConnection();
//	var mac = doc.data().mac;
//	writeData(addsocket, "1");
	writeData(addsocket,"1"+doc.data().mac);
	console.log(doc.id, '=>', doc.data().mac);
    });


  })
  .catch((err) => {
    console.log('Error getting documents', err);
  });
  
//firestore

////////////server socket
var net_server = require('net');


var server22 = net_server.createServer(function(cli) {	// receive unregistered_list from main.c and send to cloud

  console.log('Client22 connection: ');
    console.log('   local = %s:%s', cli.localAddress, cli.localPort);
    console.log('   remote = %s:%s', cli.remoteAddress, cli.remotePort);

    cli.setTimeout(500);
    cli.setEncoding('utf8');

    cli.on('data', function(data) {
        console.log('Received data from client222222222222222 on port %d: %s', cli.remotePort, data.toString());

        writeDataServer(cli, 'Sending: ' + data.toString());    // what is this???
        console.log('  Bytes sent: ' + cli.bytesWritten);
//        sendData(data);         // when receiving from main.c , publish data to cloud
        sendList(data);
    });

    cli.on('end', function() {
        console.log('Client22 disconnected');
    });

    cli.on('error', function(err) {
        console.log('Socket22 Error: ', JSON.stringify(err));
    });

    cli.on('timeout', function() {
        console.log('Socket22 Timed out');
    });
});

server22.listen(9053, function() {
    console.log('Server22 listening: ' + JSON.stringify(server22.address()));
    server.on('close', function(){
        console.log('Server22 Terminated');
    });
    server.on('error', function(err){
        console.log('Server22 Error: ', JSON.stringify(err));
    });
});


var server = net_server.createServer(function(cli) {	// receive mylist from main.c and send to cloud
  
    console.log('Client connection: ');
    console.log('   local = %s:%s', cli.localAddress, cli.localPort);
    console.log('   remote = %s:%s', cli.remoteAddress, cli.remotePort);
    
    cli.setTimeout(500);
    cli.setEncoding('utf8');
    
    cli.on('data', function(data) {
        console.log('Received data from client on port %d: %s', cli.remotePort, data.toString());
        
        writeDataServer(cli, 'Sending: ' + data.toString());	// what is this???
        console.log('  Bytes sent: ' + cli.bytesWritten);
	sendData(data); 	// when receiving from main.c , publish data to cloud 
//	sendList(data);
    });
    
    cli.on('end', function() {
        console.log('Client disconnected');
    });
    
    cli.on('error', function(err) {
        console.log('Socket Error: ', JSON.stringify(err));
    });
    
    cli.on('timeout', function() {
        console.log('Socket Timed out');
    });
});
 
server.listen(9050, function() {
    console.log('Server listening: ' + JSON.stringify(server.address()));
    server.on('close', function(){
        console.log('Server Terminated');
    });
    server.on('error', function(err){
        console.log('Server Error: ', JSON.stringify(err));
    });
});
 
function writeDataServer(socket, data){
  var success = socket.write(data);
  if (!success){
    console.log("Client Send Fail");
  }
}
//--------------------- server socket end

/////////////// getConnection for connecting server, which is main.c
var net_client = require('net'); 
function getConnection(){		/// this getConnection function is necessary
  //서버에 해당 포트로 접속 
  var socketClient = ""; 
  var recvData = [];  
  var local_port = ""; 

  socketClient = net_client.connect({port: 9090, host:'localhost'}, function() {	// connect main.c server
   
      console.log("connect log======================================================================"); 
      console.log('connect success'); 
      console.log('local = ' + this.localAddress + ':' + this.localPort); 
      console.log('remote = ' + this.remoteAddress + ':' +this.remotePort); 
   
      local_port = this.localPort; 
   
      this.setEncoding('utf8'); 
      this.setTimeout(600000); // timeout : 10분 
      console.log("client setting Encoding:binary, timeout:600000" ); 
      console.log("client connect localport : " + local_port);
  }); 

  // 접속 종료 시 처리 			
  socketClient.on('close', function() { 
      console.log("client Socket Closed : " + " localport : " + local_port); 
  }); 

// 데이터 수신 후 처리 				---> is this necessary????
/*  socketClient.on('data', function(data) { 
      console.log("data recv log======================================================================"); 	
      recvData.push(data); 	
      console.log("data.length : " + data.length);
      console.log("data recv : " + data);
//      socketClient.end();
  }); 
*/
  socketClient.on('end', function() { 
      console.log('client Socket End'); 
  }); 
   
  socketClient.on('error', function(err) { 
      console.log('client Socket Error: '+ JSON.stringify(err)); 
  }); 
   
  socketClient.on('timeout', function() { 
      console.log('client Socket timeout: '); 
  }); 
   
  socketClient.on('drain', function() { 
      console.log('client Socket drain: '); 
  }); 
   
  socketClient.on('lookup', function() { 
      console.log('client Socket lookup: '); 
  });  
  return socketClient;
}
/////////////// getConnectino end

function writeData(socket, data){
var success = socket.write(data);
if (!success){
    console.log("Server Send Fail");
}
}

 //////////////  client socket 
console.log('connecting...'); 
var client = mqtt.connect(connectionArgs); 
function sub(client){

 
// Subscribe to the /devices/{device-id}/config topic to receive config updates. 
client.subscribe('/devices/' + deviceId + '/config'); 

client.subscribe('/devices/' + deviceId + '/commands/#');
 
client.on('connect', function(success) { 
  if (success) { 
    console.log('Client connected...'); 
//    sendData(); 
  } else { 
    console.log('Client not connected...'); 
  } 
}); 
 
client.on('close', function() { 
  console.log('close'); 
}); 
 
client.on('error', function(err) { 
  console.log('error', err); 
}); 

var socketClient = null;
 
client.on('message', function(topic, message, packet) { 	/////// when receiving data from cloud or mobile, send data to main.c
  console.log(topic, 'message received: ', Buffer.from(message, 'base64').toString('ascii')); 

  if(topic == "/devices/Wireless-Node/commands/create"){	// 
	socketClient = getConnection();
	writeData(socketClient, "1");
	writeData(socketClient, message);			// message = mac address
	console.log('finalindex create!!!');
  }
  else if(topic == "/devices/Wireless-Node/commands/delete"){	// when  delete device in firestore
         socketClient = getConnection();
         writeData(socketClient, "2");
	 writeData(socketClient, message);
console.log('finalindex delete!!!');
  }
  else if(topic == "/devices/Wireless-Node/commands/menu"){   //  when require unregistered list
         socketClient = getConnection();
         writeData(socketClient, "4");
         writeData(socketClient, message);			// message = 'search'
console.log('finalindex search!!!');
  }

//  client.end();
}); 

}
/////////////////////// client socket end

sub(client);
 
function createJwt(projectId, privateKeyFile, algorithm) { 
  var token = { 
    'iat': parseInt(Date.now() / 1000), 
    'exp': parseInt(Date.now() / 1000) + 20 * 60, // 1 day 
    'aud': projectId 
  }; 

	iatTime = parseInt(Date.now() / 1000);
  var privateKey = fs.readFileSync(privateKeyFile); 
  return jwt.sign(token, privateKey, { 
    algorithm: algorithm 
  }); 
} 
 

setInterval(() => {   

  const secsFromIssue = parseInt(Date.now() / 1000) - iatTime;
  if (secsFromIssue >= 10 * 60 ) {
    iatTime = parseInt(Date.now() / 1000);
    console.log(`\tRefreshing token after ${secsFromIssue} seconds.`);

    client.end();

    connectionArgs = {
      host: mqttHost,
      port: mqttPort,
      clientId: mqttClientId,
      username: 'unused',
      password: createJwt(projectId, privateKeyFile, algorithm),
      protocol: 'mqtts',
      secureProtocol: 'TLSv1_2_method'
    };
    client = mqtt.connect(connectionArgs);
	sub(client);
  }
},1000);


function fetchData() { 
  var count = process.argv[2];
//  console.log(count);
//console.log(process.argv[3]); 
 // count = count.replace('"','');
  return count; 
} 
 
function sendData(data) { 
  var payload = data; 
 
//  payload = JSON.stringify(payload); 
  console.log(mqttTopic, ': Publishing message:', payload); 
  client.publish(mqttTopic, payload, { qos: 1 }); 
  
//   console.log('Transmitting in 30 seconds'); 
//   setTimeout(sendData, 30000); 
}

function sendList(data){
  var payload = data;

//  payload = JSON.stringify(payload);
  console.log(mqttTopic2, ': Publishing message:', payload);
  client.publish(mqttTopic2, payload, { qos: 1 });

}
