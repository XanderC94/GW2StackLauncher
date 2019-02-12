package model.objects

//import com.google.gson.*
//import java.lang.reflect.Type

data class GW2LocalAddOns(val addOns: List<String>) {

    companion object : () -> GW2LocalAddOns {

        override fun invoke(): GW2LocalAddOns {
            return GW2LocalAddOns(listOf())
        }

//        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): GW2LocalAddOns {
//            val jsonObject = json?.asJsonObject!!
//
//            return GW2LocalAddOns(
//                    jsonObject["addOns"].asJsonArray.map {
//                        it.asJsonObject["name"].asString
//                    }
//            )
//        }
    }
}