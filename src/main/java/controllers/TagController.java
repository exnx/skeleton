package controllers;

import api.CreateReceiptRequest;
import api.ReceiptResponse;
import api.TagResponse;

import dao.TagDao;
import dao.ReceiptDao;
import generated.tables.records.ReceiptsRecord;
import generated.tables.records.TagsRecord;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/tags")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagController {

    final TagDao tagDao;

    public TagController(TagDao tags) {

        this.tagDao = tags;

    }

    //return all receipts with their respective tags
    @GET
    @Path("/getAllTags")
    public List<TagResponse> getTags() {
        List<TagsRecord> tagsRecords = tagDao.getAllTags();
        return tagsRecords.stream().map(TagResponse::new).collect(toList());
    }

    // prints out all the receipts with tags
    @GET
    @Path("/{tag}")
    public List<ReceiptResponse> getAllReceiptsWithTags(@PathParam("tag") String tagName) {
        List<ReceiptsRecord> receiptRecords = tagDao.getAllReceipts(tagName);
        return receiptRecords.stream().map(ReceiptResponse::new).collect(toList());
    }

    @PUT
    @Path("/{tag}")
    public void toggleTag(@PathParam("tag") String tagName, int id) {

        // check if receipt exists
        boolean isReceiptTagged = tagDao.exists(tagName, id);
        // if it doesnt exist, create a receipt-tag relationship
        if(!isReceiptTagged) {

            int returnId = tagDao.insert(tagName,id);

            System.out.println("\n sucessfully inserted id:  " + returnId  + "sucessfully!!!\n");
        }

        // if it exists, delete this receipt-tag
        else {

            tagDao.deleteEntry(tagName,id);

            System.out.println("\n I ran the deleteEntry in TagDao " + "\n");
        }
    }
}