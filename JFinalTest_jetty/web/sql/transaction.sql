#sql("getAllTransactions")
select t.userId, t.stockTableId, t.tradePrice, t.tradeCount, t.tradeDate, t.tradeDate,
u.nickname,
s.stockId, s.stockName, s.stockType
from transaction as t,
user as u,
all_stocks as s
where t.userId = u.userId and t.stockTableId = s.stockTableId
order by t.tradeDate, t.tradeTime;
#end

#sql("getUserTransactions")
select t.stockTableId, t.tradePrice, t.tradeCount, t.tradeDate, t.tradeDate,
u.nickname,
s.stockId, s.stockName, s.stockType
from transaction as t,
user as u,
all_stocks as s
where t.userId = #para(userId)
and u.userId = t.userId
and t.stockTableId = s.stockTableId
order by t.tradeDate, t.tradeTime;
#end

#sql("updateUserPurchasingPower")
update user set purchasingPower = purchasingPower + #para(cost)
where userId = #para(userId);
#end
