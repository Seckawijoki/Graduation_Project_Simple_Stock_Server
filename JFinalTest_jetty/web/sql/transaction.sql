#sql("getTransactions")
select *
from `transaction` ;
#end

#sql("getAllTransactions")
select t.transactionId, t.userId, t.stockTableId, t.tradePrice, t.tradeCount, t.tradeDateTime,
u.nickname, s.stockName, s.stockId, s.stockType
from transaction as t, user as u, all_stocks as s
where t.userId = u.userId and t.stockTableId = s.stockTableId
order by t.tradeDateTime desc;
#end

#sql("getUserTransactions")
select t.transactionId, t.stockTableId, t.tradePrice, t.tradeCount, t.tradeDateTime,
u.nickname,
s.stockName, s.stockId, s.stockType
from transaction as t,
user as u,
all_stocks as s
where t.userId = #para(userId)
and u.userId = t.userId
and t.stockTableId = s.stockTableId
order by t.tradeDateTime desc;
#end

#sql("updateUserPurchasingPower")
update user set purchasingPower = purchasingPower + #para(cost)
where userId = #para(userId);
#end
