<h1>Smart-Invoice

Smart invoice system using Blockchains

<h2> Template

Template to be used for this application : A Java CorDapp Template.<br>
https://github.com/corda/corda-tut2-solution-java


<h2>Basis of this project

Existing tutorial sample implementations from corda :

https://github.com/corda/samples <br>
https://github.com/synechron-finlabs/Three-parties-DvP-Atomic-TX-CorDapp

<h2>Development

<h4>The three stages

1. Contract establishment  (optional or left for later )
1. IOU creation from the seller and acceptance by buyer
1. Payment vs delivery 
    1. Additional node which handles payment vs delivery atomic transaction


<h4>Begin with stage 2

*Basis* :
1. https://github.com/corda/samples/cordapp-example
1. https://github.com/corda/corda-tut2-solution-java

<h5>Sequence of process : Seller creating IOU stating buyer owes him X amount

1. Seller initiates (Initiator flow)
1. Seller uploads invoice
1. Seller creates IOU by adding the amount  and other state attributes => transaction building
1. Seller verifies => logic later
1. Seller signs the tx
1. Collects signatures
    1. implement responder(other party) logic of "buyer verifying transaction" and signing
1. Finality flow => adding to blockchain
    
<h4>Tasks to be completed

**A MUST COMPLETE TASK : Training (Backlog) completion : 
 https://docs.corda.net/index.html
 Complete the development modules until and including the tutorials section to continue the application development**

    Understand the design and the code flow of the basis project (links given above)

    Front end :
        1. Convert the url method of getting Ious, self identity(get me) etc method as tabs on a single page and convert to a multi tab front end
        2. Upon "create iou" : along with the other party name and the amount there should be an additional field for uploading the invoice
        3. add a new field : View invoice on buyer (could exist for seller as well!)
        Add/remove tasks as necessary 
        
    Back end and application :
        1. Adding invoice as attachment in the IOU transaction
        2. review/ad/remove verify strategies defined 
        3. To be edited after the understanding the existing code from basis
    
