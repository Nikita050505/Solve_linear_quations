class StackItemClass (
    private var value: String = ""
){

    val tools  = Tools()
    private var type: StackItemTypes = StackItemTypes.getType(value)

    fun getType() = type
    fun toX(){
        type = StackItemTypes.X
    }
    fun isOpen() = value == "("
    fun isClose() = value == ")"
    fun isSimpleNum(): Boolean { // Эта функция ужасно выглядит, потом исправлю
        try {
            if (value.length == 1 || value.last().toString() == "0" && value[value.lastIndex - 1].toString() == "."){
                return true
            }
            return false
        }catch (e: Exception){
            return true
        }
    }
    fun getAction() = value
    fun isNum() = !"^[*/+()\\-]$".toRegex().matches(value)
    fun isAction() = "^[*/+\\-]$".toRegex().matches(value)
    fun isPriorityAction() = "^[*/]$".toRegex().matches(value)
    fun isNotPriorityAction() = "^[+\\-]$".toRegex().matches(value)
    fun dropLast(c: Int) {value = value.dropLast(c)}
    fun addNumber(v: String) { value += v }
    fun getString() = value
    fun getValue(): Float = if(tools.findChar('x', value)) {
        if (value.length != 1) {
            tools.getString("x", value).toFloat()
        } else {
            1.0.toFloat()
        }
    }else{
        value.toFloat()
    }

    fun isX() = type == StackItemTypes.X
    fun isNotX() = type != StackItemTypes.X
}

sealed class StackItemTypes {
    object Number : StackItemTypes()
    object Action : StackItemTypes()
    object OpenBracket : StackItemTypes()
    object CloseBracket : StackItemTypes()
    object X : StackItemTypes()

    companion object {
        @JvmStatic
        fun getType(value: String): StackItemTypes {
            val tools = Tools()
            if (!"^[*/+()=\\-]$".toRegex().matches(value) && !tools.findChar('x', value)) {
                return Number
            } else if ("^[*/+\\-]$".toRegex().matches(value)) {
                return Action
            } else if (value == "(") {
                return OpenBracket
            } else if (value == ")") {
                return CloseBracket
            }else if(tools.findChar('x', value)){
                return X
            }
            throw Exception("Unknown type of stack item")
        }
    }
}
