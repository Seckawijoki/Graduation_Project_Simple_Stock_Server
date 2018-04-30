#sql("getStockRankWeightAndSpecialAttention")
select rankWeight, specialAttention
from favorite_stock
where userId = #para(userId)
and stockTableId = #para(stockTableId)
order by rankWeight desc;
#end

#sql("getAllStocks")
select stockTableId, stockId, stockType
from all_stocks
#end

#sql("updateStockCheckCount")
update all_stocks
set checkCount = checkCount + 1
where stockTableId = #para(stockTableId)
#end
