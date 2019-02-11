import java.io.File
import java.util.*

fun main(){
    var transactions = input()
    var support = 2
    var items = extractUniqueItems(transactions)
    var tableList = filterItemSet(transactions,items,support)
    calculateConfidence(tableList)

}

fun input(): ArrayList<ArrayList<String>> {
    var file  = File("input.txt")
    var input = file.bufferedReader()
    var transactions = arrayListOf<ArrayList<String>>()

    while (true) {
        var transactionRow = input.readLine()
        if (transactionRow == null) {
            break
        }
        var transaction = ArrayList<String>()
        var itemListPerTransaction = transactionRow.split(" ")
        var size = itemListPerTransaction.size
        for(i in 1 until size){
            transaction.add(itemListPerTransaction[i])
        }
        transactions.add(transaction)

    }
    return transactions

}

fun extractUniqueItems(transactions: ArrayList<ArrayList<String>>): ArrayList<String> {
    var items = arrayListOf<String>()

    for (transaction in transactions){
        for (item in transaction){
            if(!items.contains(item)){
                items.add(item)
            }
        }

    }
    items.sort()
    return items

}

fun filterItemSet(transactions: ArrayList<ArrayList<String>>,
                  items:ArrayList<String>,
                  support: Int ): ArrayList<MutableMap<ArrayList<String>, Int>> {

    var tableList = arrayListOf<MutableMap<ArrayList<String>, Int>>()

    var k = 0
    while (true) {
        k++
        var eachItemsetSupportCount = arrayListOf<Int>()
        var combinatedItemSet = getItemSet(items, k)

        for (itemSet in combinatedItemSet) {
            var count = 0
            for (transaction in transactions) {
                if (isExistInTransaction(transaction, itemSet)) {
                    count++
                }
            }
            eachItemsetSupportCount.add(count)
        }

        var minSupportItems = getItemsWithMinSupport(
            combinatedItemSet,
            eachItemsetSupportCount, support
        )

        //if (minSupportItems.isEmpty()) break


        tableList.add(minSupportItems)
        if(k==items.size) break

    }
    return tableList

}

fun getItemSet(items:ArrayList<String>, k: Int): ArrayList<ArrayList<String>>{
    if(k==1){
        var combinatedSet = arrayListOf<ArrayList<String>>()
        for(item in items){
            var temporaryList = arrayListOf<String>()
            temporaryList.add(item)
            combinatedSet.add(temporaryList)
        }
        return combinatedSet
    }
    else{

        var size = items.size
        var combinatedSet = arrayListOf<ArrayList<String>>()

        for (i in 0 until size){

            var itemsCopy = arrayListOf<String>()
            for (element in items){
                itemsCopy.add(element)
            }

            var currentItem = items[i]

            for(j in 0..i){
                itemsCopy.removeAt(0)
            }

            var combinationRemaining = getItemSet(itemsCopy, k-1)

            for (tempList in combinationRemaining){
                tempList.add(currentItem)
                tempList.sort()
                combinatedSet.add(tempList)
            }
        }

        return combinatedSet
    }
}


fun isExistInTransaction
(transaction: ArrayList<String>, itemSet: ArrayList<String>) : Boolean {
    for (item in itemSet) {
        if (!transaction.contains(item)) return false
    }
    return true
}


fun getItemsWithMinSupport
(itemSets: ArrayList<ArrayList<String>>,
 itemCount:ArrayList<Int>, support:Int): MutableMap<ArrayList<String>, Int>{

    var table = mutableMapOf<ArrayList<String>, Int>()
    for (i in 0 until itemCount.size) {
        val c = itemCount[i]
        if (c >= support) {
            table.put(itemSets[i],c)
        }
    }

    return table
}

fun calculateConfidence(tableList: ArrayList<MutableMap<ArrayList<String>, Int>>){
    var value = 0
    var unionvalue = 0
    print("Already Purchased (space separated if more than one) : ")
    var purchased = Scanner(System.`in`).nextLine()
    var items = purchased.split(" ").toMutableList()
    items.sort()
    var index = items.size -1

    if(tableList[index].containsKey(items)){
        value = tableList[index][items]!!
    }
    print("next to purchase(space separated if more than one) : ")
    var next = Scanner(System.`in`).nextLine()
    var nextItems = next.split(" ").toMutableList()
    nextItems.sort()


    var unionset = items.union(nextItems).toMutableList()
    unionset.sort()

    var unionIndex = unionset.size-1


    if(tableList[unionIndex].containsKey(unionset)){
        unionvalue = tableList[unionIndex][unionset]!!
    }

    if(unionvalue == 0) println("0%")
    else{
        var confidence = (unionvalue.toDouble()/value)*100
        println("Confidence: $confidence %")
    }
}