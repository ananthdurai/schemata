<img align="center" src="./asset/logo.png" alt="Schemata logo"/>

# Schemata

Schemata is a schema modeling framework for decentralized domain-driven ownership of data. Schemata combine a set of
standard metadata definitions for each schema & data field and a scoring algorithm to provide a feedback loop on how
efficient the data modeling of your data warehouse is. The current version of Schemata supports ProtoBuf (Thrift & Avro
support is in the future roadmap).

<p>
    <a href="https://github.com/ananthdurai/schemata/releases/"><img src="https://img.shields.io/github/v/release/ananthdurai/schemata?color=orange" alt="Latest Release"></a>   
    <a href="https://github.com/pdevito3/craftsman/blob/master/LICENSE.txt"><img src ="https://img.shields.io/github/license/ananthdurai/schemata" alt="License"></a>
  <a href="https://twitter.com/ananthdurai" target="\_parent">
    <img alt="" src="https://img.shields.io/twitter/url?style=social&url=https%3A%2F%2Ftwitter.com%2Fananthdurai" />
  </a>
</p>

------

# Design

Schemata frameworks contain two parts.

📘 **Schema metadata annotations:**

The metadata annotations enrich the context of the schema definitions.  It enforces a few mandatory metadata fields such as the owner of the schema, the domain it represents, and further classification of the data stream into Entity stream & event stream.

🎼 **Schemata Score:**

A ranking function parses all the metadata and assigns a score for each  Schema definition to define how integrated the Schema design is and validate if all the Schema definition adheres to the Schemata metadata annotations.

## Data Stream Classification

<img src="./asset/ds_classification.png" alt="Data Stream Classification"/>

At any point in time, the data producer should provide two types of data products.

### Entity

Entity streams represent the current state of the Entity. In the classical Data Warehouse concepts, Entities typically
represent the dimensions. The Entity must have a primary key field.

### Event

Event streams are typically immutable. Event streams represent the state change of an Entity. In the classical data
warehouse concepts, Event streams represent the facts. Event streams will not have a primary key field.

Events classified further into three types.

#### Type 1: Lifecycle

Lifecycle event captures the state changes of an Entity. (e.g) User created, User deleted et al.

#### Type 2: Interaction

Interaction event captures the events that resulted from one Entity changing the state of another Entity.
(e.g.) User A purchases Product B. The Interaction event is often the result of a business transaction.

#### Type 3: Aggregated

Aggregated event captures the computed metrics over a specified window of time. (e.g) Number of views by a User for a
Product.

## Schema Metadata

### Core Metadata (shared across Schema and Fields)

```protobuf
syntax = "proto3";

package org.schemata.schema;

import "google/protobuf/descriptor.proto";
// CoreMetadata is the set of attribute apply to both the Message & Field
message CoreMetadata {
  // Mandatory Metadata: description of the entity
  optional string description = 50001;
  // Optional Metadata: additional comments about the entity
  optional string comment = 50002;
  // Optional Metadata: Any related entity that has "hierarchy" or "has a"  relationships.
  optional string see_also = 50003;
  // Optional Metadata: Additional link reference for further reading.
  // It could be a confluent page, An ADR or RFC or a Slack message link.
  optional string reference = 50004;
}
```

### Schema Metadata

```protobuf
syntax = "proto3";

package org.schemata.schema;

import "google/protobuf/descriptor.proto";

extend google.protobuf.MessageOptions {

  // message.description is a Mandatory Metadata
  CoreMetadata message_core = 60001;
  // Mandatory Metadata: owner of the entity. Usually it is the team name.
  string owner = 60002;
  // Mandatory Metadata: domain = 'core' indicates the entity is common across all the domains.
  // Other possible domains are `sales`, `marketing`, `product` etc
  string domain = 60003;
  // Mandatory Metadata: define the type of the message.
  Type type = 60004;
  // Status of the entity. You can have `testing`, `production` or `staging` depends on the lifecycle of schema definition.
  string status = 60005;
  // Slack or Teams channel name to communicate with the team which owns ths entity
  string team_channel = 60006;
  // Slack or Teams channel name to alert for any validation errors.
  string alert_channel = 60007;
  // Type of the event. Set if the Type = 'EVENT'
  EventType event_type = 60008;
}
```

### Field Metadata

```protobuf
syntax = "proto3";

package org.schemata.schema;

import "google/protobuf/descriptor.proto";

extend google.protobuf.FieldOptions {
  // message.description is a Mandatory Metadata
  CoreMetadata field_core = 70001;
  // Set true if the field contains classified data (Optional).
  bool is_classified = 70002;
  // Set the classification level if is_classified is true (This is Mandatory if is_classified set to true)
  string classification_level = 7003;
  // Specify the product type. product_type is an useful annotation to represent a field in a business perspective.
  // (e.g) user_id can be an INT field, but in the system design it could represent External Users rather than internal users.
  string product_type = 70004;
  // Set true if the field is a primary key. This must be true if the Schema type is Entity
  bool is_primary_key = 70005;
}
```


🚧 **TODO:** 

Write more documentation on how schema modeling works.

# Schema Score:

Schemata Score is the core part of establishing a feedback loop to maintain the integrity of the decentralized domain ownership to build data products. Schemata construct a DirectedWeightedMultigraph to represent the data stream definitions (both Entity & Events). The Graph walk algorithm derives the Schemata Score indicating how connected each entity is.

🚧 **TODO:** 

Write more documentation on how Schemata Score works

# Curious to Try?

The code ships with an example ProtoBuf schema definition for easier understanding.

## Prerequisites

The project requires the following dependencies

1. JDK 17
2. ProtoBuf
3. Makefile
4. Maven

## How to execute

🏃 compile the project

```shell
make package or mvn clean package
```

🏃 To validate the schema definition

```shell
./validate.sh
```

🏃 To see of Schemata Score

```shell
./score.sh org.schemata.schema.CampaignCategoryTracker
```

## TODO:

🚧 Write blog post explaining the concepts in detail. 

🚧 The current code hardcoded the schema discovery. Make it more dynamic data discovery. 

🚧 Support for Avro.

🚧 Support for Thrift.

🚧 Add visualization layer to show the graph representation of the data stream.

# Contributing

Time is of the essence. Before developing a Pull Request I recommend opening a
new [topic for discussion](https://github.com/ananthdurai/schemata/discussions). It is an initial release, so I've not
added the `contributing.md`, but if you are interested, I will definitely put together a detailed writeup.

# Contact Me

Sometimes Github notifications get lost in the shuffle. If you file an issue and don't hear from me in 24-48 hours feel
free to ping me on [twitter](https://twitter.com/ananthdurai) now for easy contact with me and larger community
discussions!