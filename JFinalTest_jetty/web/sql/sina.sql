#sql("findSinaQuotation")
select stockTableId from sina_quotations
where stockTableId = #para(stockTableId)
#end

#sql("insertSinaQuotation")
insert into sina_quotations(stockTable, stockName, stockId,
currentPrice, currentPoint, fluactuationRate, turnover, volume, updateTime)
values (
#para(stockTable),
#para(stockName),
#para(stockId),
#para(currentPrice),
#para(currentPoint),
#para(fluctuationRate),
#para(turnover),
#para(volume),
updateTime = now()
)
#end

#sql("updateSinaQuotation")
update sina_quotations
set stockName = #para(stockName),
currentPrice = #para(currentPrice),
currentPoint = #para(currentPoint),
fluctuationRate = #para(fluctuationRate),
turnover = #para(turnover),
volume = #para(volume),
updateTime = now()
where stockTableId = #para(stockTableId)
#end
