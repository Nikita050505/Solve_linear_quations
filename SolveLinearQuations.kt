import me.nikitaburmitsky.calculator.lib.StackItemClass
import me.nikitaburmitsky.calculator.lib.StackItemTypes
import me.nikitaburmitsky.calculator.Tools


class SolveLinearQuations() {
    val tools = Tools()

    fun calcTwoOperands(
        op1: StackItemClass,
        sing: StackItemClass,
        op2: StackItemClass
    ): StackItemClass {

        var result = ""

        if(checkNotLinear(op1, sing, op2)){
            throw Exception("Это не линейное уравнение")
        }

        when (sing.getAction()) {
            "+" -> result = (op1.getValue() + op2.getValue()).toString()
            "-" -> result = (op1.getValue() - op2.getValue()).toString()
            "*" -> result = (op1.getValue() * op2.getValue()).toString()
            "/" -> result = (op1.getValue() / op2.getValue()).toString()
        }

        if (checkX(op1, op2)) { // Проверяет было ли одно из чисел х. Если да то в value добавляет х
            result += "x"
        }

        return StackItemClass(result)
    }

    fun checkX(op1: StackItemClass, op2: StackItemClass) = op1.isX() || op2.isX() // Проверяет было ли одно из чисел х.

    fun checkProblemWithX(op1: StackItemClass, a: StackItemClass, op2: StackItemClass) =
        op1.isX() && op2.isNotX() || op2.isX() && op1.isNotX() // Проверяет выполняется ли операция сложения/вычитания с числом с х и просто числом. Например: 10-4х.

    fun checkNotLinear(op1: StackItemClass, a: StackItemClass, op2: StackItemClass) = op1.isX() && op2.isX() && a.getString() == "*" // Проверяет не выполняются ли операциии по умножению двух х.

    fun check(op1: StackItemClass, op2: StackItemClass) =
        op1.isNotX() && op2.isNotX() || op1.isX() && op2.isX() // check проверяет наличие/отсутствие х у операндов.

    fun CSE(massiv: MutableList<StackItemClass>): MutableList<StackItemClass> {
        val mas = massiv
        val tmp = mutableListOf<StackItemClass>()
        var sign = false

        for ((i, a) in mas.withIndex()) { // Считает приоритетные операции
            sign = if (a.isPriorityAction()) {
                tmp.add(calcTwoOperands(tmp.last(), a, mas[i + 1]))
                tmp.removeAt(tmp.size - 2)
                true
            } else {
                if (!sign) {
                    tmp.add(a)
                }
                false
            }
        }

        sign = false
        val res = mutableListOf<StackItemClass>()

        for ((i, a) in tmp.withIndex()) { // Считает не приоритетные операции
            sign = if (a.isNotPriorityAction() && check(res.last(), tmp[i + 1])) { // check проверяет наличие/отсутствие х у операндов.
                if (!checkProblemWithX(res.last(), a, tmp[i + 1])) { // Проверяет выполняется ли операция сложения/вычитания с числом с х и просто числом. Например: 10-4х.
                    res.add(calcTwoOperands(res.last(), a, tmp[i + 1]))
                    res.removeAt(res.size - 2)
                }
                true
            } else {
                if (!sign) {
                    res.add(a)
                }
                false
            }
        }
        return res
    }

    fun calcBrackets(arg: MutableList<StackItemClass>): MutableList<StackItemClass> {
        var open = 0
        var close = 0

        var ar = arg

        for ((i, a) in ar.withIndex()) {
            if (a.isOpen()) {
                open = i
            } else if (a.isClose()) {
                close = i

                // Так как CSE может вернуть массив, нужно вписывать ответ по другому

                // Это массив для счёта
                var mass = ar.filterIndexed { index, s ->
                    return@filterIndexed index in (open + 1) until close
                } as MutableList<StackItemClass>

                mass = CSE(mass) // Тут отправляется для вычесления


                // Это массив для части выражения, которая находится до участка который нужно вычеслить
                val array1 = ar.filterIndexed { index, s ->
                    return@filterIndexed index < open
                } as MutableList<StackItemClass>


                // Это массив для части выражения, которая находится после участка который нужно вычеслить
                val array2 = ar.filterIndexed { index, s ->
                    return@filterIndexed index > close
                } as MutableList<StackItemClass>

                ar.clear()

                // Тут я переписываю массив ar
                for (v in array1) {
                    ar.add(v)
                }
                for (v in mass) {
                    ar.add(v)
                }
                for (v in array2) {
                    ar.add(v)
                }

                return ar
            }
        }

        val result = CSE(ar) // если не было скобок. Считает просто выражение
        ar.clear()
        for(v in result){
            ar.add(v)
        }
        return ar
    }

    fun toOpenBrackets(ar: MutableList<StackItemClass>): MutableList<StackItemClass> { // Открывает скобки

        var list = ar // Массив равный массиву данного при вызове. Нужен чтобы можно было его редактировать.
        var open = 0
        var close = 0
        for ((i, v) in list.withIndex()) {
            if (v.isOpen()) {
                open = i
            } else if (v.isClose()) {
                close = i
                var arrayForCalc = calcBrackets(tools.makeList(list, open, close)) // Массив для вычесления. Я заметил ошибку, из-за которой он просто возвращает весь исходный массив (list), но это и так работает поэтому я думаю убрать эту функцию (makeList)

                var size = arrayForCalc.size
                var previousSize = 0
                while (size != previousSize) {  // Цикл который выполняет действие счёта скобок пока длинна массива не станет равна длине до вызова функции счёта.
                    previousSize = size
                    arrayForCalc = calcBrackets(arrayForCalc)
                    size = arrayForCalc.size
                }   // В конце остаётся до конца открытый массив. Поэтому я думаю всё таки удалить эту функцию и просто сразу отдавать массив в


                // В этом блоке я изменяю основной массив, так как функция calcBrackets может вернуть не один объект а массив. Например: 10-4x .

                // Это массив для части выражения, которая находится до участка который нужно вычеслить
                val array1 = list.filterIndexed { index, s ->
                    return@filterIndexed index < tools.getIndLeft()
                } as MutableList<StackItemClass>

                // Это массив для части выражения, которая находится после участка который нужно вычеслить
                val array2 = list.filterIndexed { index, s ->
                    return@filterIndexed index > tools.getIndRight()
                } as MutableList<StackItemClass>

                list.clear()

                // Тут я переписываю массив list
                for (v in array1) {
                    list.add(v)
                }
                for (v in arrayForCalc) {
                    list.add(v)
                }
                for (v in array2) {
                    list.add(v)
                }

                break  // Выход из цикла к return
            }
        }
        return list
    }

    fun main(): MutableList<StackItemClass> {  //Возвращает резульат
        var arr = tools.translate("(3+2)*2-(2+2)*x=2")  // Переводит строку в два массива: до равно и после. Сейчас пока решается только до равно.
        var ar = toOpenBrackets(arr[0])   // Вызов функции для открытия скобок
        return ar //Возвращает результат
    }
}

val f = SolveLinearQuations()

fun main() {    // Выводит результат в консоль
    val ar = f.main()
    for (j in ar) {
        print(j.getString())
    }
}
