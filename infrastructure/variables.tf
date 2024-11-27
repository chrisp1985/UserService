variable "confluent_key" {
  description = "Confluent Cloud API Key"
  type        = string
  sensitive   = true
  default     = "cloud-api-key-default"
}

variable "confluent_secret" {
  description = "Confluent Cloud API Secret"
  type        = string
  sensitive   = true
  default     = "cloud-api-secret-default"
}

variable "confluent_schema_key" {
  description = "Confluent Cloud Schema API Key"
  type        = string
  sensitive   = true
  default     = "confluent-schema-api-key-default"
}

variable "confluent_schema_secret" {
  description = "Confluent Cloud Schema API Secret"
  type        = string
  sensitive   = true
  default     = "confluent-schema-api-secret-default"
}

variable "aws_access_key" {
  description = "AWS API Key"
  type        = string
  sensitive   = true
  default     = "aws-keys-default"
}

variable "aws_access_secret" {
  description = "AWS API Secret"
  type        = string
  sensitive   = true
  default     = "aws-secret-default"
}

variable "image_tag" {
  description = "Image Tag pulled from Github workflow."
  type        = string
  sensitive   = false
  default     = "image-tag-default"
}