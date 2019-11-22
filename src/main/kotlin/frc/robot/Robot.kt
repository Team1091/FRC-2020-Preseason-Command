/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot

import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.command.Scheduler
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer
import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.vision.VisionThread
import frc.robot.commands.ExampleCommand
import frc.robot.subsystems.ExampleSubsystem
import edu.wpi.first.wpilibj.RobotDrive



/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
class Robot : TimedRobot() {

    private var networkTable:NetworkTableInstance? = null
    private var gripNetworkTable:NetworkTable? = null

    init{
        networkTable = NetworkTableInstance.getDefault()
        gripNetworkTable = NetworkTableInstance.getDefault().getTable("GRIP/myBlobsReport")

    }

    //Network Tables

    private val IMG_WIDTH = 320
    private val IMG_HEIGHT = 240

    private val visionThread: VisionThread? = null
    private val centerX = 0.0

    private val imgLock = Any()

    internal var m_autonomousCommand: Command? = null
    internal var m_chooser = SendableChooser<Command>()

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    override fun robotInit() {
        //Camera Crap
        var camera:UsbCamera = UsbCamera("Camera",0)
        camera.setResolution(640,480)
        CameraServer.getInstance().startAutomaticCapture(camera)

        m_oi = OI()
        m_chooser.setDefaultOption("Default Auto", ExampleCommand())
        // chooser.addOption("My Auto", new MyAutoCommand());
        SmartDashboard.putData("Auto mode", m_chooser)
    }

    /**
     * This function is called every robot packet, no matter the mode. Use
     * this for items like diagnostics that you want ran during disabled,
     * autonomous, teleoperated and test.
     *
     *
     * This runs after the mode specific periodic functions, but before
     * LiveWindow and SmartDashboard integrated updating.
     */
    override fun robotPeriodic() {
        var x = gripNetworkTable?.getEntry("x")?.getDoubleArray(doubleArrayOf())!!
        var y = gripNetworkTable?.getEntry("y")?.getDoubleArray(doubleArrayOf())!!
        var size = gripNetworkTable?.getEntry("size")?.getDoubleArray(doubleArrayOf())!!

        var blobs = arrayListOf<VisionBlob>()
        for ((index, value) in x.withIndex()){
            var blob = VisionBlob()
            blob.x = x[index]
            blob.y = y[index]
            blob.size = size[index]
            //blobs.add(blob)
            println("Blob $index : $blob")
        }

        //println("x: ${x.firstOrNull() ?: "N/A"}, y: ${y.firstOrNull() ?: "N/A"}, size: ${size.firstOrNull() ?: "N/A"}")
    }

    /**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
     * the robot is disabled.
     */
    override fun disabledInit() {}

    override fun disabledPeriodic() {
        Scheduler.getInstance().run()
    }

    /**
     * This autonomous (along with the chooser code above) shows how to select
     * between different autonomous modes using the dashboard. The sendable
     * chooser code works with the Java SmartDashboard. If you prefer the
     * LabVIEW Dashboard, remove all of the chooser code and uncomment the
     * getString code to get the auto name from the text box below the Gyro
     *
     *
     * You can add additional auto modes by adding additional commands to the
     * chooser code above (like the commented example) or additional comparisons
     * to the switch structure below with additional strings & commands.
     */
    override fun autonomousInit() {
        m_autonomousCommand = m_chooser.selected

        /*
     * String autoSelected = SmartDashboard.getString("Auto Selector",
     * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
     * = new MyAutoCommand(); break; case "Default Auto": default:
     * autonomousCommand = new ExampleCommand(); break; }
     */

        // schedule the autonomous command (example)
        if (m_autonomousCommand != null) {
            m_autonomousCommand!!.start()
        }
    }

    /**
     * This function is called periodically during autonomous.
     */
    override fun autonomousPeriodic() {
        Scheduler.getInstance().run()
    }

    override fun teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (m_autonomousCommand != null) {
            m_autonomousCommand!!.cancel()
        }
    }

    /**
     * This function is called periodically during operator control.
     */
    override fun teleopPeriodic() {
        Scheduler.getInstance().run()
    }

    /**
     * This function is called periodically during test mode.
     */
    override fun testPeriodic() {}

    companion object {
        var m_subsystem = ExampleSubsystem()
        var m_oi = OI()
    }
}
