#sql("getStockRankWeightAndSpecialAttention")
select rankWeight, specialAttention
from favorite_stock
where userId = #para(userId)
and stockTableId = #para(stockTableId)
order by rankWeight desc;
#end