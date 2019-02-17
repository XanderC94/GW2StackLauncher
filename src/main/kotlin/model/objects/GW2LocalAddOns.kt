package model.objects

//import com.google.gson.*
//import java.lang.reflect.Type

data class GW2LocalAddOns(val addOns: List<GW2LocalAddOn>) {

    companion object : () -> GW2LocalAddOns {

        override fun invoke(): GW2LocalAddOns {
            return GW2LocalAddOns(listOf())
        }
    }
}