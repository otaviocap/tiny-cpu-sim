module tinycpu.tinycpu {
    requires javafx.controls;
    requires javafx.fxml;


    opens tinycpu.simulator to javafx.fxml;
    exports tinycpu.simulator;
    exports tinycpu.simulator.Controllers;
    opens tinycpu.simulator.Controllers to javafx.fxml;
    exports tinycpu.simulator.Engine.Exceptions;
    opens tinycpu.simulator.Engine.Exceptions to javafx.fxml;
    exports tinycpu.simulator.Engine;
    opens tinycpu.simulator.Engine to javafx.fxml;
    exports tinycpu.simulator.Engine.Instructions;
    opens tinycpu.simulator.Engine.Instructions to javafx.fxml;
    exports tinycpu.simulator.Engine.Memory;
    opens tinycpu.simulator.Engine.Memory to javafx.fxml;
}