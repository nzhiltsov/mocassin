ErrorStats <- function(logs){
  errors <- ErrorDocuments(logs)
  u <- unique(errors$Reason)
  f <- function(reason) {nrow(subset(errors, Reason==reason))}
  stats <- ldply(u, function(elem) f(elem))
  colnames(stats)[1] <- "Count"
  frame <- as.data.frame(cbind(u, stats), stringsAsFactors = F)
  colnames(frame)[1] <- "Reason"
  frame
}