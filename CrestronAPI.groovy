/*
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
	path("/thermostats/:id/:command/:temp") {
		action: [
			GET: "updateThermostat"
		]
	}
	path("/thermostats/:id/:command/") {
		action: [
			GET: "updateThermostat"
		]
	}    
    
}

def installed() {
	subscribe(switches, "switch", switchesHandler)
    subscribe(dimmers, "level", dimmersHandler)
    subscribe(thermostats, "temperature", handleTemperatureEvent)
	subscribe(thermostats, "heatingSetpoint", handleHeatingSetpointEvent)
	subscribe(thermostats, "coolingSetpoint", handleCoolingSetpointEvent)
	subscribe(thermostats, "thermostatMode", handleThermostatModeEvent)
	subscribe(thermostats, "thermostatFanMode", handleFanModeEvent)
	subscribe(presence, "presence", presenceHandler)
//    update(thermostats)
}

def updated() {
	unsubscribe()
	subscribe(switches, "switch", switchesHandler)
    subscribe(dimmers, "level", dimmersHandler)
    subscribe(thermostats, "temperature", handleTemperatureEvent)
	subscribe(thermostats, "heatingSetpoint", handleHeatingSetpointEvent)
	subscribe(thermostats, "coolingSetpoint", handleCoolingSetpointEvent)
	subscribe(thermostats, "thermostatMode", handleThermostatModeEvent)
	subscribe(thermostats, "thermostatFanMode", handleFanModeEvent)
    log.debug "subscribed ThermostatMode"
	subscribe(presence, "presence", presenceHandler)
//    update(thermostats)
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
  def device = thermostats.find { it.id == params.id }
  //def currenttemp = device.coolingSetpoint()
  def command = params.command
  def temp = params.temp
  //int tempint = params.temp as Integer
  if(command == 'tempup') {
    device.raiseSetpoint()
  }
  else if(command == 'tempdown') {
    device.lowerSetpoint()
  }
  else if(command == 'setcoolingsetpoint'){
  	device.setCoolingSetpoint(temp as int)
    }
  else if(command == 'setheatingsetpoint'){
  	device.setHeatingSetpoint(temp as int)
    }
  else if(command == 'setthermostatfanmode'){
  	device.setThermostatFanMode(temp)
    }
  else if(command == 'setthermostatmode'){
  	device.setThermostatMode(temp)
    }
    device.poll()
    log.debug "Thermostat Updated"
    
}


private SendUpdate(String path) {
    def pollParams = [
        uri: "Your Public IP:PORT",
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

//def thermostatsHandler(evt) {
//	log.debug "thermostatsHandler success"
//   if (evt.name == "temperature") {
//     SendUpdate("/${evt.deviceId}=Temp=${evt.value}")
//    }
//    if (evt.name == "heatingSetpoint") {
//        SendUpdate("/${evt.deviceId}/=HeatSP=${evt.value}")
//    }
//    if (evt.name == "coolingSetpoint") {
//        SendUpdate("/${evt.deviceId}/=CoolSP=${evt.value}")
//    }
//    if (evt.name == "thermostatMode") {
//        SendUpdate("/${evt.deviceId}/=SystemMode=${evt.value}")
//    }
//    if (evt.name == "thermostatFanMode") {
//        SendUpdate("/${evt.deviceId}/=FanMode=${evt.value}")
//   }
//	
//}

def handleHeatingSetpointEvent(evt) {
log.debug "thermostatsHandler success"
SendUpdate("/${evt.deviceId}/=HeatSP=${evt.value}")
	}

def handleCoolingSetpointEvent(evt) {
log.debug "thermostatsHandler success"
log.debug "handleCoolingSetpointEvent ${evt.value}"
        SendUpdate("/${evt.deviceId}/=CoolSP=${evt.value}")
	}


def handleThermostatModeEvent(evt) {
log.debug "thermostatsHandler success"
log.debug "handleThermostatModeEvent ${evt.value}"
        SendUpdate("/${evt.deviceId}/=SystemMode=${evt.value}")
	}

def handleFanModeEvent(evt) {
log.debug "thermostatsHandler success"
log.debug "handleFanModeEvent ${evt.value}"
        SendUpdate("/${evt.deviceId}/=FanMode=${evt.value}")
	}
def handleTemperatureEvent(evt) {
log.debug "thermostatsHandler success"
log.debug "handleTemperatureEvent ${evt.value}"
        SendUpdate("/${evt.deviceId}/=temperature=${evt.value}")
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