ErrorDocuments <- function(logs){
  require(stringr)
  require(plyr)
  error.entries <- 
    subset(logs, Level == "ERROR" &
    Logger == "r.k.n.c.m.u.d.server.MathnetAdapter" &
    regexpr("Failed to handle a document", Message) > 0)
  
  docId <- ldply(error.entries$Message, function(s) str_match(s, "ivm[0-9]+"))
  colnames(docId)[1] <- "docId"
  
  r <- rownames(error.entries)
  error.messages <- ldply(r, function(s) {i <- as.numeric(s) + 1; logs[i,]$Date})
  colnames(error.messages)[1] <- "Message"
  
  GetReason <- function(message){
    reason = NA
    if (regexpr("Not normal output while producing arxmliv document", message) > 0) {
      reason = "latexml"
    } else if (regexpr("Not normal output while compiling PDF", message) > 0) {
      reason = "pdflatex"
    }
    reason
  }
  reasons <- ldply(error.messages$Message, GetReason)
  colnames(reasons)[1] <- "Reason"
  as.data.frame(cbind(docId, reasons, error.messages), stringsAsFactors = F)
}