/*
 * Copyright (C) 2024, FRC Team 3602. All rights reserved. This work
 * is licensed under the terms of the MIT license which can be found
 * in the root directory of this project.
 */

package frc.team3602.robot.vision;

import java.util.Optional;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonUtils;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Radians;
import static frc.team3602.robot.Constants.VisionConstants.*;

public class Vision {
  private final AprilTagFieldLayout kFieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();

  private final PhotonCamera photonCamera = new PhotonCamera(kPhotonCameraName);
  private final PhotonPoseEstimator photonPoseEstimator = new PhotonPoseEstimator(kFieldLayout,
      PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, photonCamera, kRobotToCamera);

  private double lastEstimateTimestamp = 0.0;

  public Vision() {
    configVision();
  }

  public PhotonPipelineResult getLatestResult() {
    return photonCamera.getLatestResult();
  }

  public Optional<EstimatedRobotPose> getEstimatedRobotPose() {
    var visionEstimate = photonPoseEstimator.update();
    double latestTimestamp = getLatestResult().getTimestampSeconds();
    boolean newResult = Math.abs(latestTimestamp - lastEstimateTimestamp) > 1e-5;

    if (newResult) {
      lastEstimateTimestamp = latestTimestamp;
    }

    return visionEstimate;
  }

  public double getTargetHeight() {
    double targetHeight;
    var result = getLatestResult();

    if (result.hasTargets()) {
      targetHeight = kFieldLayout.getTagPose(result.getBestTarget().getFiducialId()).get().getZ();
    } else {
      targetHeight = 0.0;
    }

    return targetHeight;
  }

  public double getTargetRange(Measure<Distance> targetHeight) {
    double targetRange;
    var result = getLatestResult();

    if (result.hasTargets()) {
      targetRange = PhotonUtils.calculateDistanceToTargetMeters(kCameraHeight.in(Meters), targetHeight.in(Meters),
          kCameraPitch.in(Radians), Units.degreesToRadians(result.getBestTarget().getPitch()));
    } else {
      targetRange = 0.0;
    }

    return targetRange;
  }

  private void configVision() {
    photonPoseEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);
  }
}
