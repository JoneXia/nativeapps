cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/org/www/org.underscorejs.underscore/underscore.js",
        "id": "org.underscorejs.underscore"
    },
    {
        "file": "plugins/org/www/org.bcsphere/bc.js",
        "id": "org.bcsphere.bcjs"
    },
    {
        "file": "plugins/org/www/org.bcsphere.bluetooth/bluetoothapi.js",
        "id": "org.bcsphere.bluetooth.bluetoothapi",
        "merges": [
            "navigator.bluetooth"
        ]
    },
    {
        "file": "plugins/org/www/org.bluetooth.profile/proximity.js",
        "id": "org.bluetooth.profile.proximity",
        "merges": [
            "BC"
        ]
    },
    {
        "file": "plugins/org/www/org.bluetooth.profile/find_me.js",
        "id": "org.bluetooth.profile.find_me",
        "merges": [
            "BC"
        ]
    },
    {
        "file": "plugins/org/www/org.bluetooth.profile/serial_port.js",
        "id": "org.bluetooth.profile.serial_port",
        "merges": [
            "BC"
        ]
    },
    {
        "file": "plugins/org/www/org.bluetooth.service/battery_service.js",
        "id": "org.bluetooth.service.battery_service",
        "merges": [
            "BC"
        ]
    },
    {
        "file": "plugins/org/www/org.bluetooth.service/blood_pressure.js",
        "id": "org.bluetooth.service.blood_pressure",
        "merges": [
            "BC"
        ]
    },
    {
        "file": "plugins/org/www/org.bluetooth.service/health_thermometer.js",
        "id": "org.bluetooth.service.health_thermometer",
        "merges": [
            "BC"
        ]
    },
    {
        "file": "plugins/org/www/org.bluetooth.service/immediate_alert.js",
        "id": "org.bluetooth.service.immediate_alert",
        "merges": [
            "BC"
        ]
    },
    {
        "file": "plugins/org/www/org.bluetooth.service/link_loss.js",
        "id": "org.bluetooth.service.link_loss",
        "merges": [
            "BC"
        ]
    },
    {
        "file": "plugins/org/www/org.bluetooth.service/serial_port.js",
        "id": "org.bluetooth.service.serial_port",
        "merges": [
            "BC"
        ]
    },
    {
        "file": "plugins/org/www/org.bluetooth.service/tx_power.js",
        "id": "org.bluetooth.service.tx_power",
        "merges": [
            "BC"
        ]
    },
    {
        "file": "plugins/org/www/org.bcsphere.ibeacon/ibeaconapi.js",
        "id": "org.bcsphere.ibeacon.ibeaconapi",
        "merges": [
            "navigator.ibeacon"
        ]
    },
    {
        "file": "plugins/org/www/org.bcsphere/ibeacon.js",
        "id": "org.bcsphere.ibeacon",
        "merges": [
            "BC"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.camera/www/CameraConstants.js",
        "id": "org.apache.cordova.camera.Camera",
        "clobbers": [
            "Camera"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.camera/www/CameraPopoverOptions.js",
        "id": "org.apache.cordova.camera.CameraPopoverOptions",
        "clobbers": [
            "CameraPopoverOptions"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.camera/www/Camera.js",
        "id": "org.apache.cordova.camera.camera",
        "clobbers": [
            "navigator.camera"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.camera/www/CameraPopoverHandle.js",
        "id": "org.apache.cordova.camera.CameraPopoverHandle",
        "clobbers": [
            "CameraPopoverHandle"
        ]
    },
    {
        "file": "plugins/org.bcsphere.camera/www/camera.js",
        "id": "org.bcsphere.camera.camera",
        "clobbers": [
            "BCCamera"
        ]
    },
    {
        "file": "plugins/org.bcsphere.telephony/www/telephony.js",
        "id": "org.bcsphere.telephony.telephony",
        "clobbers": [
            "Telephony"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.dialogs/notification.js",
        "id": "org.apache.cordova.dialogs.notification",
        "clobbers": [
            "navigator.notification"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.geolocation/www/Coordinates.js",
        "id": "org.apache.cordova.geolocation.Coordinates",
        "clobbers": [
            "Coordinates"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.geolocation/www/PositionError.js",
        "id": "org.apache.cordova.geolocation.PositionError",
        "clobbers": [
            "PositionError"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.geolocation/www/Position.js",
        "id": "org.apache.cordova.geolocation.Position",
        "clobbers": [
            "Position"
        ]
    },
     {
        "file": "plugins/org.apache.cordova.geolocation/www/geolocation.js",
        "id": "org.apache.cordova.geolocation.geolocation",
        "clobbers": [
            "Geolocation"
        ]
    },

];
module.exports.metadata = 
// TOP OF METADATA
{
    "org": "0.5.0",
    "org.apache.cordova.camera": "0.3.3",
    "org.bcsphere.camera": "0.1.0",
}
// BOTTOM OF METADATA
});