SuccessfulDocuments <- function(logs){
  require(stringr)
  require(plyr)
  success.entries <- 
    subset(logs, Level == "INFO " &
    Logger == "r.k.n.c.m.u.d.server.MathnetAdapter" &
    regexpr("has been processed", Message) > 0)

  docId <- ldply(success.entries$Message, function(s) str_match(s, "ivm[0-9]*"))
  colnames(docId)[1] <- "docId"
  elapsedTime <- 
    ldply(success.entries$Message, function(s) str_match(s, " [0-9]+ "))
  elapsedTime$V1 <- as.numeric(elapsedTime$V1)
  colnames(elapsedTime)[1] <- "elapsedTime"
  as.data.frame(cbind(docId, elapsedTime), stringsAsFactors = F)
}
