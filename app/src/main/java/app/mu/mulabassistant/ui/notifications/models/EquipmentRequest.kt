package app.mu.mulabassistant.ui.notifications.models

import app.mu.mulabassistant.ui.home.models.Equipment

data class EquipmentRequest (

    val cost:Double?=null,

    val days: Int?=null,

    val equipment: Equipment?=null,

    val equipmentAdmin:String?=null,

    val isApproved:Boolean?=null,

    val isPaid:Boolean?=null,

    val requestendday:Long?=null,

    val requeststartday:Long?=null,

    val userId:String?=null,
        )