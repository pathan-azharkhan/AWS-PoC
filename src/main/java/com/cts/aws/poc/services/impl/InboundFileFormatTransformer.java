/**
 * 
 */
package com.cts.aws.poc.services.impl;

import iso.std.iso._20022.tech.xsd.pain_001_001.CreditTransferTransactionInformation10;
import iso.std.iso._20022.tech.xsd.pain_001_001.Document;
import iso.std.iso._20022.tech.xsd.pain_001_001.GroupHeader32;
import iso.std.iso._20022.tech.xsd.pain_001_001.PaymentInstructionInformation3;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.cts.aws.poc.models.PaymentBatch;
import com.cts.aws.poc.models.PaymentInstruction;
import com.cts.aws.poc.models.PaymentParty;
import com.cts.aws.poc.services.FileFormatTransformer;
import com.cts.aws.poc.utils.DateUtils;

/**
 * @author Azharkhan
 *
 */
@Component
public class InboundFileFormatTransformer implements FileFormatTransformer<Document, PaymentBatch> {

	@Override
	public PaymentBatch transform(Document input) {
		
		PaymentBatch batch = new PaymentBatch();
		
		GroupHeader32 grpHdr = input.getCstmrCdtTrfInitn().getGrpHdr();
		
		batch.setBatchId(grpHdr.getMsgId());
		batch.setTotalTxns(Integer.parseInt(grpHdr.getNbOfTxs()));
		batch.setTotalAmnt(grpHdr.getCtrlSum().doubleValue());
		
		// FIXME: Introduce null checks
		List<PaymentInstructionInformation3> pmtInfList = input.getCstmrCdtTrfInitn().getPmtInf();
		
		List<PaymentInstruction> pmntInstructions = new ArrayList<>(pmtInfList.size());
		
		pmtInfList.forEach(pmtInf -> {
			
			PaymentInstruction pmntInstr = new PaymentInstruction();
			
			CreditTransferTransactionInformation10 cdtTrfTxInf = pmtInf.getCdtTrfTxInf().get(0);
			
			pmntInstr.setInstrctnId(cdtTrfTxInf.getPmtId().getInstrId());
			pmntInstr.setValueDate(DateUtils.gregorianCalendarToLocalDate(pmtInf.getReqdExctnDt()));
			pmntInstr.setTxnAmnt(cdtTrfTxInf.getAmt().getInstdAmt().getValue().doubleValue());
			pmntInstr.setCurrency(cdtTrfTxInf.getAmt().getInstdAmt().getCcy());
			
			PaymentParty debtor = new PaymentParty();
			PaymentParty creditor = new PaymentParty();
			
			debtor.setPartyId(pmtInf.getDbtr().getId().getOrgId().getOthr().get(0).getId());
			debtor.setAccountId(pmtInf.getDbtrAcct().getId().getIBAN());
			debtor.setBankCode(pmtInf.getDbtr().getId().getOrgId().getBICOrBEI());
			
			creditor.setPartyId(cdtTrfTxInf.getCdtr().getId().getOrgId().getOthr().get(0).getId());
			creditor.setAccountId(cdtTrfTxInf.getCdtrAcct().getId().getIBAN());
			creditor.setBankCode(cdtTrfTxInf.getCdtr().getId().getOrgId().getBICOrBEI());
			
			pmntInstr.setDebtor(debtor);
			pmntInstr.setCreditor(creditor);
			
			pmntInstructions.add(pmntInstr);
		});
		
		batch.setPayments(pmntInstructions);
		
		return batch;
	}
}