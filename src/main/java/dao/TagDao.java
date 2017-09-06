package dao;

import api.ReceiptResponse;
import generated.tables.records.TagsRecord;
import generated.tables.records.ReceiptsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static generated.Tables.RECEIPTS;
import static generated.Tables.TAGS;

public class TagDao {
    DSLContext dsl;

    public TagDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    // check to see if receipt already tagged by using a query
    public boolean exists(String tag, int id) {

        Result tagsRecord = dsl.select().from(TAGS)
                .where(TAGS.ID.eq(id))
                .and(TAGS.TAG.eq(tag))
                .fetch();

        int sizeCheck = tagsRecord.size();

        if(sizeCheck == 1) {
            return true;
        }

        else {
            return false;
        }
    }

    // inserts a new relationship, tagging a receipt that matches
    public int insert(String tag, int receiptId) {
        TagsRecord tagsRecord = dsl
                .insertInto(TAGS,TAGS.TAG,TAGS.RECEIPTID)
                .values(tag,receiptId)
                .returning(TAGS.ID)
                .fetchOne();

        checkState(tagsRecord != null && tagsRecord.getId() != null, "Insert failed");

        return tagsRecord.getId();
    }

    // deletes a tag on a receipt
    public void deleteEntry(String tagName, int id) {

        dsl.delete(TAGS)
                .where(TAGS.ID.eq(id))
                .and(TAGS.TAG.eq(tagName))
                .execute();
    }

    // print out all the existing receipts with tags
    public List<ReceiptsRecord> getAllReceipts(String tagName) {
        List<ReceiptsRecord> receiptRecord = dsl.select()
                .from(RECEIPTS)
                .join(TAGS)
                .on(TAGS.RECEIPTID.eq(RECEIPTS.ID))
                .where(TAGS.TAG.eq(tagName))
                .fetchInto(RECEIPTS);
        return receiptRecord;

    }
}
