package org.schemata.compatibility;

public record Summary(String filename, String schemaName, String fieldName, String fieldType) {

    private Summary(Builder builder) {
        this(builder.filename, builder.schemaName, builder.fieldName, builder.fieldType);
    }

    public static class Builder {
        protected String filename;
        protected String schemaName;
        protected String fieldName;
        protected String fieldType;

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder schemaName(String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

        public Builder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Builder fieldType(String fieldType) {
            this.fieldType = fieldType;
            return this;
        }

        public Summary build() {
            return new Summary(filename, schemaName, fieldName, fieldType);
        }
    }
}

