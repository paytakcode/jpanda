package com.kakao.jPanda.jst.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakao.jPanda.jst.dao.TradeDao;
import com.kakao.jPanda.jst.domain.StatDto;
import com.kakao.jPanda.jst.domain.TalentDto;
import com.kakao.jPanda.jst.domain.TradeListDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class TradeServiceImpl implements TradeService{
	
	private final TradeDao tradeDao;
	
	@Autowired
	public TradeServiceImpl(TradeDao tradeDao) {
		this.tradeDao = tradeDao;
	}
	
	@Override
	public StatDto getStat(String memberId) {
		return tradeDao.getStatById(memberId);
	}
	
	@Override
	public List<TradeListDto> getTradeList(String memberId, TradeListDto tradeListDto) {
		Map<String, Object> paraMap = new HashMap<>();
		paraMap.put("memberId", memberId);
		paraMap.put("tradeListDto", tradeListDto);
		
		List<TradeListDto> tradeList = tradeDao.getTradeList(paraMap);
		
		tradeList.stream()
				 .filter(DeduplicationUtils.distinctByKey(e -> e.getTalentNo()))
				 .forEach(e -> {
					 if (memberId.equals(e.getBuyerId()) && e.getRefundStatus() == null) {
 						 e.setListType("buy");
						 e.setStatus("구매완료");
					 } else if (memberId.equals(e.getBuyerId()) && e.getRefundStatus() != null) {
					 	 e.setListType("refund");
					 } else if (memberId.equals(e.getSellerId())) {
						 e.setListType("sell");
					 } 
				 });
		
		log.info("getTradeList tradeList.size() : " + tradeList.size());
		
		return tradeList;
	}

	@Override
	public int endSell(String talentNo) {
		
		return tradeDao.updateTalentStatus(talentNo);
	}

	@Override
	public int cancleRefund(String purchaseNo) {
		return tradeDao.updateRefundStatus(purchaseNo);
	}

	@Override
	public TalentDto getTalentByTalentNo(String talentNo) {
		return tradeDao.selectTalent(talentNo);
	}

	@Override
	public int submitExchange(TalentDto talentDto) {
		
		return tradeDao.insertExchange(talentDto);
	}

}//end class

//이전코드

//@Override
//public List<SellListDto> getSellList(String memberId) {
//	return tradeDao.getSellListById(memberId);
//}
//
//@Override
//public List<BuyListDto> getBuyList(String memberId) {
//	List<BuyListDto> buyList = tradeDao.getBuyListById(memberId);
//	buyList.forEach(e -> e.setStatus("구매완료"));
//	return buyList;
//}
//
//@Override
//public List<RefundListDto> getRefundList(String memberId) {
//	return tradeDao.getRefundListById(memberId);
//}
//
//
//@Override
///**
// *  listType을 셋팅하여 Ajax 통신시 콜백함수 if문에서 조건으로 사용
// */
//public List<TradeListDto> getAllList(String memberId) {
//	List<TradeListDto> allList = tradeDao.getAllListById(memberId);
//	allList.forEach(e -> {
//		if (memberId.equals(e.getBuyerId()) && e.getRefundStatus() == null) {
//			e.setListType("구매");
//			e.setStatus("구매완료");
//		} else if (memberId.equals(e.getBuyerId()) && e.getRefundStatus() != null) {
//			e.setListType("환불");
//		} else if (memberId.equals(e.getSellerId())) {
//			e.setListType("판매");
//		} 
//		
//	});
//	
//	return allList;
//}
