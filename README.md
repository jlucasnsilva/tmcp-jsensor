# TMCP

Basic simulation of the algorithm Tree-Based Multichannel Protocol (proposed
in the paper "Efficient Multichannel Communications in Wireless Sensor Networks")
run in the simulator [JSensor](https://joubertlimadotcomdotbr.wordpress.com/jsensor-a-high-performance-java-simulator-for-sensor-networks/).

This the part one of the assignment of "Projetos e Aplicações em Redes de Sensores"
(COMP 318, COMP 319 e ECOM 088) at the Universidade Federal de Alagoas.

# Paper
Efficient Multichannel Communications in Wireless Sensor Networks

- YAFENG WU, University of Virginia
- KIN SUM LIU, Stony Brook University
- JOHN A. STANKOVIC, University of Virginia
- TIAN HE, University of Minnesota
- SHAN LIN, Stony Brook University

ACM Transactions on Sensor Networks (TOSN), Volume 12 Issue 1, March 2016.

# Bugs

- At some stage, for still unknown reason, the algorithm fails to allocate channels. Hence some nodes are cut from communications.

# Differences Between this Implementation and the Proposed

- Channel interference: instead of being calculated as the number of node using
that channel, it's calculated as the number of children of a node;
- The algorithm doesn't work into two steps (Fat-tree construction then channel
allocation). Instead, once a node is added to the fat-tree, it commands its
children join it and request a channel from its parents;

# Configuring
Besides the Tmcp.config file required by the JSensor, one can set the fields:
- Sink.NUM_OF_CHANNELS
- Sensor.WORKING_NODES_PROPORTION
- Sensor.RELATIVE_TIME
- Sensor.MAX_NUM_OF_MESSAGES
