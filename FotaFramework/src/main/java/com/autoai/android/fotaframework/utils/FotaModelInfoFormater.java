package com.autoai.android.fotaframework.utils;

import com.autoai.android.aidl.FotaAidlModelInfo;
import com.autoai.android.fota.model.FOTAModelInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyanchao on 2018/8/8.
 */

public class FotaModelInfoFormater {

    public static FotaAidlModelInfo format(FOTAModelInfo fotaModelInfo) {
        if (fotaModelInfo == null) return null;

        FotaAidlModelInfo fotaAidlModelInfo = new FotaAidlModelInfo();
        fotaAidlModelInfo.setId(fotaModelInfo.getId());
        fotaAidlModelInfo.setModelID(fotaModelInfo.getModelID());
        fotaAidlModelInfo.setModelName(fotaModelInfo.getModelName());
        fotaAidlModelInfo.setModelCurrentVersion(fotaModelInfo.getModelCurrentVersion());
        fotaAidlModelInfo.setSystemCurrentVersion(fotaModelInfo.getSystemCurrentVersion());
        fotaAidlModelInfo.setModelUpdateTime(fotaModelInfo.getModelUpdateTime());

        List<FOTAModelInfo.UpdateModelTaskInfo> updateModelTaskInfoList = fotaModelInfo.getUpdateModelTaskInfoList();
        if (updateModelTaskInfoList != null && updateModelTaskInfoList.size() > 0) {
            List<FotaAidlModelInfo.UpdateModelTaskInfo> aidlUpdateModelTaskInfoList = new ArrayList<FotaAidlModelInfo.UpdateModelTaskInfo>();
            for (FOTAModelInfo.UpdateModelTaskInfo updateModelTaskInfo : updateModelTaskInfoList) {
                FotaAidlModelInfo.UpdateModelTaskInfo aidlUpdateModelTaskInfo = new FotaAidlModelInfo.UpdateModelTaskInfo();
                aidlUpdateModelTaskInfo.setId(updateModelTaskInfo.getId());
                aidlUpdateModelTaskInfo.setOrderNum(updateModelTaskInfo.getOrderNum());
                aidlUpdateModelTaskInfo.setUpdateVersion(updateModelTaskInfo.getUpdateVersion());
                aidlUpdateModelTaskInfo.setSystemMinVersion(updateModelTaskInfo.getSystemMinVersion());
                aidlUpdateModelTaskInfo.setSystemMaxVersion(updateModelTaskInfo.getSystemMaxVersion());
                aidlUpdateModelTaskInfo.setDownloadFileSize(updateModelTaskInfo.getDownloadFileSize());
                aidlUpdateModelTaskInfo.setDownloadFileDownloadURL(updateModelTaskInfo.getDownloadFileDownloadURL());
                aidlUpdateModelTaskInfo.setModelUpdateVersionDescription(updateModelTaskInfo.getModelUpdateVersionDescription());
                aidlUpdateModelTaskInfo.setEncryptionStr(updateModelTaskInfo.getEncryptionStr());
                aidlUpdateModelTaskInfo.setMustUpdate(updateModelTaskInfo.getMustUpdate());
                aidlUpdateModelTaskInfo.setVehicleType(updateModelTaskInfo.getVehicleType());
                aidlUpdateModelTaskInfo.setModelID(updateModelTaskInfo.getModelID());
                aidlUpdateModelTaskInfo.setModelName(updateModelTaskInfo.getModelName());
                aidlUpdateModelTaskInfo.setSummary(updateModelTaskInfo.getSummary());
                aidlUpdateModelTaskInfo.setType(updateModelTaskInfo.getType());
                aidlUpdateModelTaskInfo.setStartVersion(updateModelTaskInfo.getStartVersion());
                aidlUpdateModelTaskInfo.setTargetVersion(updateModelTaskInfo.getTargetVersion());
                aidlUpdateModelTaskInfo.setShowVersion(updateModelTaskInfo.getShowVersion());
                aidlUpdateModelTaskInfo.setMd5(updateModelTaskInfo.getMd5());
                aidlUpdateModelTaskInfo.setSecretKey(updateModelTaskInfo.getSecretKey());
                aidlUpdateModelTaskInfo.setPackageType(updateModelTaskInfo.getPackageType());
                aidlUpdateModelTaskInfo.setDownloadState(updateModelTaskInfo.getDownloadState());
                aidlUpdateModelTaskInfo.setVerifyState(updateModelTaskInfo.getVerifyState());
                aidlUpdateModelTaskInfo.setDecodeState(updateModelTaskInfo.getDecodeState());
                aidlUpdateModelTaskInfo.setUpdateState(updateModelTaskInfo.getUpdateState());

                List<FOTAModelInfo.UpdateModelTaskInfo.UpdateRequirement> updateRequirementList = updateModelTaskInfo.getUpdateRequirementList();
                if (updateRequirementList != null && updateRequirementList.size() > 0) {
                    List<FotaAidlModelInfo.UpdateModelTaskInfo.UpdateRequirement> aidlUpdateRequirementList = new ArrayList<FotaAidlModelInfo.UpdateModelTaskInfo.UpdateRequirement>();
                    for (FOTAModelInfo.UpdateModelTaskInfo.UpdateRequirement updateRequirement : updateRequirementList) {
                        FotaAidlModelInfo.UpdateModelTaskInfo.UpdateRequirement aidlUpdateRequirement = new FotaAidlModelInfo.UpdateModelTaskInfo.UpdateRequirement();
                        aidlUpdateRequirement.setId(updateRequirement.getId());
                        aidlUpdateRequirement.setModelId(updateRequirement.getModelId());
                        aidlUpdateRequirement.setModelName(updateRequirement.getModelName());
                        aidlUpdateRequirement.setRequirement(updateRequirement.getRequirement());
                        aidlUpdateRequirement.setTaskId(updateRequirement.getTaskId());
                        aidlUpdateRequirement.setVersion(updateRequirement.getVersion());
                        aidlUpdateRequirementList.add(aidlUpdateRequirement);
                    }
                    aidlUpdateModelTaskInfo.setUpdateRequirementList(aidlUpdateRequirementList);
                }
                aidlUpdateModelTaskInfoList.add(aidlUpdateModelTaskInfo);
            }
            fotaAidlModelInfo.setUpdateModelTaskInfoList(aidlUpdateModelTaskInfoList);
        }

        List<FOTAModelInfo.UploadInfo> uploadInfoList = fotaModelInfo.getUploadInfoList();
        if (uploadInfoList != null && uploadInfoList.size() > 0) {
            List<FotaAidlModelInfo.UploadInfo> aidlUploadInfoList = new ArrayList<FotaAidlModelInfo.UploadInfo>();
            for (FOTAModelInfo.UploadInfo uploadInfo : uploadInfoList) {
                FotaAidlModelInfo.UploadInfo aidlUploadInfo = new FotaAidlModelInfo.UploadInfo();
                aidlUploadInfo.setModelFilePath(uploadInfo.getModelFilePath());
                aidlUploadInfo.setModelFileSize(uploadInfo.getModelFileSize());
                aidlUploadInfo.setModelUpdateVersionDescription(uploadInfo.getModelUpdateVersionDescription());
                aidlUploadInfo.setSummary(uploadInfo.getSummary());
                aidlUploadInfo.setSystemMaxVersion(uploadInfo.getSystemMaxVersion());
                aidlUploadInfo.setSystemMinVersion(uploadInfo.getSystemMinVersion());
                aidlUploadInfo.setUpdateVersion(uploadInfo.getUpdateVersion());
                aidlUploadInfo.setMustUpdate(uploadInfo.isMustUpdate());
                aidlUploadInfoList.add(aidlUploadInfo);
            }
            fotaAidlModelInfo.setUploadInfoList(aidlUploadInfoList);
        }
        return fotaAidlModelInfo;
    }

    public static FOTAModelInfo format(FotaAidlModelInfo fotaAidlModelInfo) {
        if (fotaAidlModelInfo == null) return null;

        FOTAModelInfo fotaModelInfo = new FOTAModelInfo();
        fotaModelInfo.setId(fotaAidlModelInfo.getId());
        fotaModelInfo.setModelID(fotaAidlModelInfo.getModelID());
        fotaModelInfo.setModelName(fotaAidlModelInfo.getModelName());
        fotaModelInfo.setModelCurrentVersion(fotaAidlModelInfo.getModelCurrentVersion());
        fotaModelInfo.setSystemCurrentVersion(fotaAidlModelInfo.getSystemCurrentVersion());
        fotaModelInfo.setModelUpdateTime(fotaAidlModelInfo.getModelUpdateTime());

        List<FotaAidlModelInfo.UpdateModelTaskInfo> aidlUpdateModelTaskInfoList = fotaAidlModelInfo.getUpdateModelTaskInfoList();
        if (aidlUpdateModelTaskInfoList != null && aidlUpdateModelTaskInfoList.size() > 0) {
            List<FOTAModelInfo.UpdateModelTaskInfo> updateModelTaskInfoList = new ArrayList<FOTAModelInfo.UpdateModelTaskInfo>();
            for (FotaAidlModelInfo.UpdateModelTaskInfo aidlUpdateModelTaskInfo : aidlUpdateModelTaskInfoList) {
                FOTAModelInfo.UpdateModelTaskInfo updateModelTaskInfo = new FOTAModelInfo.UpdateModelTaskInfo();
                updateModelTaskInfo.setId(aidlUpdateModelTaskInfo.getId());
                updateModelTaskInfo.setOrderNum(aidlUpdateModelTaskInfo.getOrderNum());
                updateModelTaskInfo.setUpdateVersion(aidlUpdateModelTaskInfo.getUpdateVersion());
                updateModelTaskInfo.setSystemMinVersion(aidlUpdateModelTaskInfo.getSystemMinVersion());
                updateModelTaskInfo.setSystemMaxVersion(aidlUpdateModelTaskInfo.getSystemMaxVersion());
                updateModelTaskInfo.setDownloadFileSize(aidlUpdateModelTaskInfo.getDownloadFileSize());
                updateModelTaskInfo.setDownloadFileDownloadURL(aidlUpdateModelTaskInfo.getDownloadFileDownloadURL());
                updateModelTaskInfo.setModelUpdateVersionDescription(aidlUpdateModelTaskInfo.getModelUpdateVersionDescription());
                updateModelTaskInfo.setEncryptionStr(aidlUpdateModelTaskInfo.getEncryptionStr());
                updateModelTaskInfo.setMustUpdate(aidlUpdateModelTaskInfo.getMustUpdate());
                updateModelTaskInfo.setVehicleType(aidlUpdateModelTaskInfo.getVehicleType());
                updateModelTaskInfo.setModelID(aidlUpdateModelTaskInfo.getModelID());
                updateModelTaskInfo.setModelName(aidlUpdateModelTaskInfo.getModelName());
                updateModelTaskInfo.setSummary(aidlUpdateModelTaskInfo.getSummary());
                updateModelTaskInfo.setType(aidlUpdateModelTaskInfo.getType());
                updateModelTaskInfo.setStartVersion(aidlUpdateModelTaskInfo.getStartVersion());
                updateModelTaskInfo.setTargetVersion(aidlUpdateModelTaskInfo.getTargetVersion());
                updateModelTaskInfo.setShowVersion(aidlUpdateModelTaskInfo.getShowVersion());
                updateModelTaskInfo.setMd5(aidlUpdateModelTaskInfo.getMd5());
                updateModelTaskInfo.setSecretKey(aidlUpdateModelTaskInfo.getSecretKey());
                updateModelTaskInfo.setPackageType(aidlUpdateModelTaskInfo.getPackageType());
                updateModelTaskInfo.setDownloadState(aidlUpdateModelTaskInfo.getDownloadState());
                updateModelTaskInfo.setVerifyState(aidlUpdateModelTaskInfo.getVerifyState());
                updateModelTaskInfo.setDecodeState(aidlUpdateModelTaskInfo.getDecodeState());
                updateModelTaskInfo.setUpdateState(aidlUpdateModelTaskInfo.getUpdateState());

                List<FotaAidlModelInfo.UpdateModelTaskInfo.UpdateRequirement> aidlUpdateRequirementList = aidlUpdateModelTaskInfo.getUpdateRequirementList();
                if (aidlUpdateRequirementList != null && aidlUpdateRequirementList.size() > 0) {
                    List<FOTAModelInfo.UpdateModelTaskInfo.UpdateRequirement> updateRequirementList = new ArrayList<FOTAModelInfo.UpdateModelTaskInfo.UpdateRequirement>();
                    for (FotaAidlModelInfo.UpdateModelTaskInfo.UpdateRequirement aidlUpdateRequirement : aidlUpdateRequirementList) {
                        FOTAModelInfo.UpdateModelTaskInfo.UpdateRequirement updateRequirement = new FOTAModelInfo.UpdateModelTaskInfo.UpdateRequirement();
                        updateRequirement.setId(aidlUpdateRequirement.getId());
                        updateRequirement.setModelId(aidlUpdateRequirement.getModelId());
                        updateRequirement.setModelName(aidlUpdateRequirement.getModelName());
                        updateRequirement.setRequirement(aidlUpdateRequirement.getRequirement());
                        updateRequirement.setTaskId(aidlUpdateRequirement.getTaskId());
                        updateRequirement.setVersion(aidlUpdateRequirement.getVersion());
                        updateRequirementList.add(updateRequirement);
                    }
                    updateModelTaskInfo.setUpdateRequirementList(updateRequirementList);
                }
                updateModelTaskInfoList.add(updateModelTaskInfo);
            }
            fotaModelInfo.setUpdateModelTaskInfoList(updateModelTaskInfoList);
        }

        List<FotaAidlModelInfo.UploadInfo> aidlUploadInfoList = fotaAidlModelInfo.getUploadInfoList();
        if (aidlUploadInfoList != null && aidlUploadInfoList.size() > 0) {
            List<FOTAModelInfo.UploadInfo> uploadInfoList = new ArrayList<FOTAModelInfo.UploadInfo>();
            for (FotaAidlModelInfo.UploadInfo aidlUploadInfo : aidlUploadInfoList) {
                FOTAModelInfo.UploadInfo uploadInfo = new FOTAModelInfo.UploadInfo();
                uploadInfo.setModelFilePath(aidlUploadInfo.getModelFilePath());
                uploadInfo.setModelFileSize(aidlUploadInfo.getModelFileSize());
                uploadInfo.setModelUpdateVersionDescription(aidlUploadInfo.getModelUpdateVersionDescription());
                uploadInfo.setSummary(aidlUploadInfo.getSummary());
                uploadInfo.setSystemMaxVersion(aidlUploadInfo.getSystemMaxVersion());
                uploadInfo.setSystemMinVersion(aidlUploadInfo.getSystemMinVersion());
                uploadInfo.setUpdateVersion(aidlUploadInfo.getUpdateVersion());
                uploadInfo.setMustUpdate(aidlUploadInfo.isMustUpdate());
                uploadInfoList.add(uploadInfo);
            }
            fotaModelInfo.setUploadInfoList(uploadInfoList);
        }
        return fotaModelInfo;
    }

    public static List<FotaAidlModelInfo> formatList(List<FOTAModelInfo> fotaModelInfoList) {
        if (fotaModelInfoList == null) return null;

        ArrayList<FotaAidlModelInfo> fotaAidlModelInfoList = new ArrayList<FotaAidlModelInfo>();
        for (FOTAModelInfo fotaModelInfo : fotaModelInfoList) {
            FotaAidlModelInfo fotaAidlModelInfo = format(fotaModelInfo);
            if (fotaAidlModelInfo != null) {
                fotaAidlModelInfoList.add(fotaAidlModelInfo);
            }
        }
        return fotaAidlModelInfoList;
    }

    public static List<FOTAModelInfo> formatAidlList(List<FotaAidlModelInfo> fotaAidlModelInfoList) {
        if (fotaAidlModelInfoList == null) return null;

        ArrayList<FOTAModelInfo> fotaModelInfoList = new ArrayList<FOTAModelInfo>();
        for (FotaAidlModelInfo fotaAidlModelInfo : fotaAidlModelInfoList) {
            FOTAModelInfo fotaModelInfo = format(fotaAidlModelInfo);
            if (fotaModelInfo != null) {
                fotaModelInfoList.add(fotaModelInfo);
            }
        }
        return fotaModelInfoList;
    }

}
