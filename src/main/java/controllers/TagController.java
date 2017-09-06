package controllers;

import api.CreateReceiptRequest;
import api.ReceiptResponse;
import dao.TagDao;
import dao.ReceiptDao;
import generated.tables.records.ReceiptsRecord;

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

    // finish this !

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