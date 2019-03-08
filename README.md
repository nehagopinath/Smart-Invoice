# Smart-Invoice

Smart invoice system using Blockchains

# Template
Template to be used for this application : A Java CorDapp Template.
- https://github.com/corda/corda-tut2-solution-java


#Basis of this project :
existing tutorial sample implementations from corda :

https://github.com/corda/samples
https://github.com/synechron-finlabs/Three-parties-DvP-Atomic-TX-CorDapp


#The three stages
    1. Contract establishment  (optional or left for later )
    2. IOU creation from the seller and acceptance by buyer
    3. Payment vs delivery 
        - Additional node which handles payment vs delivery atomic transaction

#Development

- Begin with stage 2 :
    - https://github.com/corda/samples/cordapp-example
    - Basis : https://github.com/corda/corda-tut2-solution-java

##Sequence of process : Seller creating IOU stating buyer owes him X amount
    1. Seller initiates (Initiator flow)
    2. Seller uploads invoice
    3. Seller creates IOU by adding the amount  and other state attributes => transaction building
    4. Seller verifies => logic later
    5. Seller signs the tx
    6. Collects signatures
        6.1. implement responder(other party) logic of "buyer verifying transaction" and signing
    7. Finality flow => adding to blockchain
    
#Tasks to be completed by :

** A MUST COMPLETE TASK : Training (Backlog) completion : 
 https://docs.corda.net/index.html
 Complete the development modules until and including the tutorials section to continue the application development**

    Understand the design and the code flow of the basis project (links given above)

    Front end :
        1. Convert the url method of getting Ious, self identity(get me) etc method as tabs on a single page and convert to a multi tab front end
        2. Upon "create iou" : along with the other party name and the amount there should be an additional field for uploading the invoice
        3. add a new field : View invoice on buyer (could exist for seller as well!)
        Add/remove tasks as necessary 
        
    Back end :
        1. Adding invoice as attachment in the IOU transaction
        2. review/ad/remove verify strategies defined 
        3. To be edited after the understanding the existing code from basis
    
