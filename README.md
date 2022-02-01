![Badge em Desenvolvimento](http://img.shields.io/static/v1?label=STATUS&message=EM%20DESENVOLVIMENTO&color=GREEN&style=for-the-badge)

# SmallCPU Simulator

SmallCPU-Sim is an JavaFX-based open-source project for an instruction-level simulator for the teaching-purpose central processing unit SmallCPU.

![Screenshot of SmallCPU simulator graphical user interface](https://user-images.githubusercontent.com/27533879/152024108-aefc9f88-48ad-4471-a4c9-a169722a66b9.png)

### Main functionalities:
- Graphical simulation environment
- Instruction manager interface
- Step-by-step simulation interface
- Load and save memories states from/to files

### Adopted languagens and libraries:
- `Java 8`
- `JavaFX`
- `Netbeans project`

## SmallCPU - A teaching-purpose central processing unit

### Basic Specification

- 8-bit processor
- Four general-purpose registers: RA, RB, RC and RX (used for indexed memory addressing)
- Memory addessing modes: direct, immediate and indexed
- Arithmetic and logic unit (ALU):
  - 8-bit 2-complement arithmetic operations
  - Adding (SelULA=0) and subtraction (SelULA=1) operations
  - Condition codes: Z (zero) and N (negative)

### Instruction Set Architecture

| Instruction | Adressing mode | Assembly |  Execution |
| ----------- | -------- | ---------- | -- |
| **LDR** (load register) | Direct | LDR _reg_ _mem_ | _reg_ ← DATA_MEM\[_mem_\] |
| **LDR** (load register) | Immediate | LDR _reg_ #_value_ | _reg_ ← _value_ |
| **LDR** (load register) | Indexed | LDR _reg_ _offset_,X | _reg_ ← DATA_MEM\[RX + _offset_\] |
| **STR** (store register) | Direct | STR _reg_ _mem_ | DATA_MEM\[_mem_\] ← _reg_ |
| **STR** (store register) | Indexed | STR _reg_ _offset_,X | DATA_MEM\[RX+_offset_\] ← _reg_ |
| **ADD** (arithmetic add) | Direct | ADD _reg_ _mem_ | _reg_ ← _reg_ + DATA_MEM\[_mem_\] |
| **ADD** (arithmetic add) | Immediate | ADD _reg_ #_value_ | _reg_ ← _reg_ + _value_ |
| **ADD** (arithmetic add) | Indexed | ADD _reg_ _offset_,X | _reg_ ← _reg_ + DATA_MEM\[RX + _offset_\] |
| **SUB** (arithmetic subtraction) | Direct | SUB _reg_ _mem_ | _reg_ ← _reg_ - DATA_MEM\[_mem_\] |
| **SUB** (arithmetic subtraction) | Immediate | SUB _reg_ #_value_ | _reg_ ← _reg_ - _value_ |
| **SUB** (arithmetic subtraction) | Indexed | SUB _reg_ _offset_,X | _reg_ ← _reg_ - DATA_MEM\[RX + _offset_\] |
| **JMP** (unconditional jump) | Direct | JMP _mem_ | PC ← _mem_ |
| **JC** (conditional jump) | Direct | JC _cc_ _mem_ | (PC ← _mem_) if (_cc_ = Z and Z = 1) or (_cc_ = N and N = 1) |
| **HLT** (halt) | | HLT | _suspend execution_ |


### RTL-Level Schematic

![SmallCPU RTL-level block diagram](https://user-images.githubusercontent.com/27533879/151595348-9dbd5bc9-4ce2-44da-98b2-ff6834ef27e8.png)
