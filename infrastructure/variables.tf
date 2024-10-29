variable "cloud_cluster_api_key" {
  description = "Confluent Cloud API Key"
  type        = string
  sensitive   = true
  default     = "cloud api key default"
}

variable "cloud_cluster_api_secret" {
  description = "Confluent Cloud API Secret"
  type        = string
  sensitive   = true
  default     = "cloud api secret default"
}

variable "unique_id" {
  description = "Unique Id from Github sha."
  type        = string
  sensitive   = true
  default     = "unique id default"
}

variable "image_tag" {
  description = "Image Tag pulled from Github workflow."
  type        = string
  sensitive   = false
  default     = "image tag default"
}