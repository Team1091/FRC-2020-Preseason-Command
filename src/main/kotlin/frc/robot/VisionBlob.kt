package frc.robot

class VisionBlob {
    var x = -1.0
    var y = -1.0
    var size = -1.0

    override fun toString(): String {
        if(x < 0 || y< 0 || size < 0)
            return "No Blob Found!"
        return "x: $x | y: $y | size: $size"
    }
}