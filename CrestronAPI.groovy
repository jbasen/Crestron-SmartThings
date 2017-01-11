/**
 *  Crestron Integration
 *
 *  Author: SmartThings
 */
definition(
    name: "CrestronAPI",
    namespace: "sytanek",
    author: "Brannon Bowden",
    description: "Send Message to Crestron System",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/text_presence.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/text_presence@2x.png"
)

preferences {
	section("Trigger API Updates when these devices change state...") {
        input "switches", "capability.switch", title: "Which Switches?", multiple: true, required: false
        input "dimmers", "capability.switchLevel", title: "Which Dimmers?", multiple: true, required: false
        input "locks", "capability.lock", title: "Which Locks?", multiple: true, required: false
        input "presence", "capability.presenceSensor", title: "Which Presence Sensors?", multiple: true, required: false
        input "thermostats", "capability.thermostat", title: "Which Thermostats?", multiple: true, required: false
	}
}

mappings {

	path("/switches") {
		action: [
			GET: "listSwitches"
		]
	}
	path("/switches/:id") {
		action: [
			GET: "showSwitch"
		]
	}
	path("/switches/:id/:command") {
		action: [
			GET: "updateSwitch"
		]
	}
	path("/dimmers") {
		action: [
			GET: "listDimmers"
		]
	}
	path("/dimmers/:id") {
		action: [
			GET: "showDimmer"
		]
	}
	path("/dimmers/:id/:command/:level") {
		action: [
			GET: "updateDimmer"
		]
	}    
	path("/locks") {
		action: [
			GET: "listLocks"
		]
	}
	path("/locks/:id") {
		action: [
			GET: "showLock"
		]
	}
	path("/locks/:id/:command") {
		action: [
			GET: "updateLock"
		]
	}    
    	path("/presence") {
		action: [
			GET: "listPresence"
		]
	}
	path("/presence/:id") {
		action: [
			GET: "showPresence"
		]
	}
	path("/presence/:id/:command") {
		action: [
			GET: "updatePresence"
		]
	}
    	path("/thermostats") {
		action: [
			GET: "listThermostats"
		]
	}
	path("/thermostats/:id") {
		action: [
			GET: "showThermostat"
		]
	}
	path("/thermostats/:id/:command") {
		action: [
			GET: "updateThermostat"
		]
	}        
    
}

def installed() {
	subscribe(switches, "switch", switchesHandler)
    subscribe(dimmers, "level", dimmersHandler)
    subscribe(thermostats, "thermostat", thermostatsHandler)
	subscribe(presence, "presence", presenceHandler)
}

def updated() {
	unsubscribe()
	subscribe(switches, "switch", switchesHandler)
    subscribe(dimmers, "level", dimmersHandler)
    subscribe(thermostats, "thermostat", thermostatsHandler)
	subscribe(presence, "presence", presenceHandler)
}

//switches
def listSwitches() {
	switches.collect{device(it,"switch")}
}

def showSwitch() {
	show(switches, "switch")
}
void updateSwitch() {
	update(switches)
}

//dimmers
def listDimmers() {
	dimmers.collect{device(it,"dimmer")}
}

def showDimmer() {
	show(dimmers, "dimmer")
}
void updateDimmer() {
	update(dimmers)
}

//locks
def listLocks() {
	locks.collect{device(it,"lock")}
}

def showLock() {
	show(locks, "lock")
}

void updateLock() {
	update(locks)
}

//presence
def listPresence() {
	presence.collect{device(it,"presence")}
}

def showPresence() {
	show(presence, "presence")
}

void updatePresence() {
	update(presence)
}

//thermostats
def listThermostats() {
	thermostats.collect{device(it,"thermostat")}
}

def showThermostat() {
	show(thermostats, "thermostat")
}
void updateThermostat() {
	update(thermostat)
}


private SendUpdate(String path) {
    def pollParams = [
        uri: "http://YOUR_PUBLIC_IP:PORT",
        path: "${path}"
        ]
	httpGet(pollParams)
}

def switchesHandler(evt) {	
	if (evt.value == "on") {
		SendUpdate("/${evt.deviceId}/on")
	} 
    else if (evt.value == "off") {
		SendUpdate("/${evt.deviceId}/off")
	}
}

def dimmersHandler(evt) {
    if (evt.value != null){
    	SendUpdate("/${evt.deviceId}/${evt.value}")
    }
}

def thermostatsHandler(evt) {
	if (evt.name == "temperature") {
    	SendUpdate("/set/Serial/1/${evt.deviceId}=Temp=${evt.value}")
	}
    if (evt.name == "heatingSetpoint") {
    	SendUpdate("/set/Serial/1/${evt.deviceId}=HeatSP=${evt.value}")
	}
    if (evt.name == "coolingSetpoint") {
    	SendUpdate("/set/Serial/1/${evt.deviceId}=CoolSP=${evt.value}")
	}
    if (evt.name == "thermostatMode") {
    	SendUpdate("/set/Serial/1/${evt.deviceId}=SystemMode=${evt.value}")
	}
    if (evt.name == "thermostatFanMode") {
    	SendUpdate("/set/Serial/1/${evt.deviceId}=FanMode=${evt.value}")
	}

}

def presenceHandler(evt) {
	if (evt.value == "present") {
		SendUpdate("/${evt.deviceId}/present")
	} 
    else if (evt.value == "not present") {
		SendUpdate("/${evt.deviceId}/not present")
	}	
}

private show(devices, type) {
	def device = devices.find { it.id == params.id }
	if (!device) {
		httpError(404, "Device not found")
	}
	else {
    	def attributeName = type;
        if (type == "motion") {
        	attributeName = "motionSensor"
        }
        if (type == "dimmer") {
        	attributeName = "level"
        }
        
        if (type == "presence") {
        	attributeName = "presence"
        }
		
        def currentState = device.currentState("level")        

		def s = device.currentState(attributeName)
		[id: device.id, label: device.displayName, value: s?.value, unitTime: s?.date?.time, type: type]
	}
}

private void update(devices) {
	log.debug "update, request: params: ${params}, devices: $devices.id"
    
    
	//def command = request.JSON?.command
    def command = params.command
    def level = params.level
    //let's create a toggle option here
	if (command) 
    {
		def device = devices.find { it.id == params.id }
		if (!device) {
			httpError(404, "Device not found")
		} else {
        	if(command == "toggle")
       		{
            	if(device.currentValue('switch') == "on")
                  device.off();
                else
                  device.on();
       		}
        	else if(command == "level")
       		{
            	device.setLevel(level as Integer)
       		}            
       		else
       		{
				device."$command"()
            }
		}
	}
}

private device(it, type) {
	it ? [id: it.id, label: it.label, type: type] : null
}