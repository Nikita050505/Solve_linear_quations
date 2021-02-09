import me.nikitaburmitsky.calculator.lib.StackItemTypes
import me.nikitaburmitsky.calculator.lib.StackItemClass
import java.text.NumberFormat

class Tools {
    private var indexLeft = 0
    private var indexRight = 0

    fun findChar(a: Char, v: String): Boolean {
        for (i in v) {
            if (i == a) {
                return true
            }
        }
        return false
    }

    fun getString(outString: String, v: String): String {
        var result = ""
        for (i in v) {
            if (i.toString() != outString) {
                result += i.toString()
            }
        }
        return result
    }

    fun translate(string: String): Array<MutableList<StackItemClass>> {
        var i = 0
        val arr = arrayOf(
            mutableListOf<StackItemClass>(),
            mutableListOf<StackItemClass>()
        )
        for (v in string) {
            if (arr[i].isEmpty()) {
                arr[i].add(StackItemClass(v.toString()))
            } else {

                if ("^[*/+\\-]$".toRegex().matches(v.toString())) {
                    arr[i].add(StackItemClass(v.toString()))
                } else if (
                    arr[i].last().getType() == StackItemTypes.Number &&
                    StackItemClass(v.toString()).getType() == StackItemTypes.Number
                ) {
                    arr[i].last().addNumber(v.toString())
                } else if (v == 'x' && arr[i].last().getType() == StackItemTypes.Number) {
                    arr[i].last().addNumber("x")
                    arr[i].last().toX()
                } else if (!"^[*/+=\\-]$".toRegex().matches(v.toString())) {
                    arr[i].add(StackItemClass(v.toString()))
                } else if (v == '=') {
                    i++
                } else {
                    throw Exception("Неизвестный знак")
                }
            }
        }
        return arr
    }

    fun findIndex(ar: MutableList<StackItemClass>, str: String): Int {
        for((i, a) in ar.withIndex()){
            if(a.getString() == str){
                return i
            }
        }
        throw Exception("Char isn't find")
    }

    fun getIndLeft() = indexLeft
    fun getIndRight() = indexRight

    fun countNumbers(ar: MutableList<String>): Int {
        var count = 0
        for (v in ar){
            if (!"^[*+/\\-]$".toRegex().matches(v)){
                count++
            }
        }
        return count
    }

    fun makeList(list: MutableList<StackItemClass>, open: Int, close: Int): MutableList<StackItemClass> {
        var array = mutableListOf<StackItemClass>()
        var indexRange = 1
        if (open!= 0 && list[open - 1].isPriorityAction()){
            var objectOfList = list[open - indexRange]
            loop@ while (objectOfList.isPriorityAction() && objectOfList.isAction() || objectOfList.isNum()){
                array.add(list[open - indexRange])
                indexRange++
                if(open - indexRange < 0){
                    break@loop
                }
                objectOfList = list[open - indexRange]
            }
            if(objectOfList.isNotPriorityAction()){
                array.removeAt(array.lastIndex)
            }
            indexLeft = open - indexRange
        }

        array.reverse()

        val tbf = list.filterIndexed{ index, s ->
            return@filterIndexed index in open..close
        }as MutableList<StackItemClass>

        for(i in tbf){
            array.add(i)
        }

        indexRange = 1

        if (close != list.lastIndex && list[close + 1].isPriorityAction()){
            var objectOfList = list[close + indexRange]
            while (objectOfList.isPriorityAction() && objectOfList.isAction() || objectOfList.isNum() || objectOfList.isX()){
                array.add(list[close + indexRange])
                indexRange++
                if(close + indexRange > list.size - 1){
                    break
                }
                objectOfList = list[close + indexRange]
            }
        }
        indexRight = close + indexRange
        return list
    }
}
