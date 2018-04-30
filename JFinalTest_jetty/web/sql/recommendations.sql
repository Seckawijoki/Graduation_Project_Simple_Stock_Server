#sql("getHotStocks")
select a.* from sina_quotations as a, all_stocks as b
where a.stockTableId = b.stockTableId
order by b.checkCount desc
limit #para(hotStockCount)
;
#end

#sql("getTopSeveralFluctuationRate")
select * from sina_quotations
order by fluctuationRate desc
limit #para(topStockCount);
#end

#sql("getBottomSeveralFluctuationRate")
select * from sina_quotations
order by fluctuationRate asc
limit #para(topStockCount);
#end

#sql("getTopSeveralCurrentPrice")
select * from sina_quotations
order by currentPrice desc
limit #para(topStockCount);
#end

#sql("getBottomSeveralCurrentPrice")
select * from sina_quotations
order by currentPrice asc
limit #para(topStockCount);
#end

#sql("getTopSeveralTurnover")
select * from sina_quotations
order by turnover desc
limit #para(topStockCount);
#end

#sql("getTopSeveralVolume")
select * from sina_quotations
order by volume desc
limit #para(topStockCount);
#end
