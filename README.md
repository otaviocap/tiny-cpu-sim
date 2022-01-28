# SmallCPU Simulator

SmallCPU-Sim is an JavaFX-based open-source project for an instruction-level simulator for the teaching-purpose Central Processing Unit SmallCPU.

## SmallCPU

### Basic Specification

- 8-bit processor
- Four general-purpose registers: RA, RB, RC and RX (used for indexed memory addressing)
- Memory addessing modes: direct, immediate and indexed
- Arithmetic and logic unit (ALU):
  - 8-bit 2-complement arithmetic operations
  - Adding (SelULA=0) and subtraction (SelULA=1) operations
  - Condition codes: Z (zero) and N (negative)

### RTL-Level Schematic

![SmallCPU RTL-level block diagram] (https://user-images.githubusercontent.com/27533879/151595348-9dbd5bc9-4ce2-44da-98b2-ff6834ef27e8.png)
