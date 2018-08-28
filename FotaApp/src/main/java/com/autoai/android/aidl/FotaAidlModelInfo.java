package com.autoai.android.aidl;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyanchao on 2018/8/28.
 */

public class FotaAidlModelInfo implements Parcelable {
    private long id;//数据库自增id
    private int modelID;
    private String modelName;

    private int modelCurrentVersion = -1;//model当前的版本号
    private int systemCurrentVersion = -1;//当前系统内核版本
    private long modelUpdateTime = -1;//更新时间

    private List<UpdateModelTaskInfo> updateModelTaskInfoList;//升级任务信息
    private List<UploadInfo> uploadInfoList;//升级包信息


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * 获得modelID
     *
     * @return modelID
     */
    public int getModelID() {
        return modelID;
    }

    /**
     * 设置modelID
     *
     * @param modelID modelID
     */
    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    /**
     * 获得model名称
     *
     * @return model名称
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * 设置model名称
     *
     * @param modelName model名称
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 获得model当前的版本号
     *
     * @return model当前的版本号
     */
    public int getModelCurrentVersion() {
        return modelCurrentVersion;
    }

    /**
     * 设置model当前的版本号
     *
     * @param modelCurrentVersion model当前的版本号
     */
    public void setModelCurrentVersion(int modelCurrentVersion) {
        this.modelCurrentVersion = modelCurrentVersion;
    }

    /**
     * 获得当前系统内核版本
     *
     * @return 当前系统内核版本
     */
    public int getSystemCurrentVersion() {
        return systemCurrentVersion;
    }

    /**
     * 设置当前系统内核版本
     *
     * @param systemCurrentVersion 当前系统内核版本
     */
    public void setSystemCurrentVersion(int systemCurrentVersion) {
        this.systemCurrentVersion = systemCurrentVersion;
    }

    /**
     * 获得更新时间
     *
     * @return 更新时间
     */
    public long getModelUpdateTime() {
        return modelUpdateTime;
    }

    /**
     * 设置更新时间
     *
     * @param modelUpdateTime 更新时间
     */
    public void setModelUpdateTime(long modelUpdateTime) {
        this.modelUpdateTime = modelUpdateTime;
    }

    /**
     * 获取升级任务
     *
     * @return 升级任务
     */
    public List<UpdateModelTaskInfo> getUpdateModelTaskInfoList() {
        return updateModelTaskInfoList;
    }

    /**
     * 设置升级任务
     *
     * @param updateModelTaskInfoList 升级任务
     */
    public void setUpdateModelTaskInfoList(List<UpdateModelTaskInfo> updateModelTaskInfoList) {
        this.updateModelTaskInfoList = updateModelTaskInfoList;
    }

    /**
     * 获取升级包的信息
     *
     * @return 升级包的信息
     */
    public List<UploadInfo> getUploadInfoList() {
        return uploadInfoList;
    }

    /**
     * 设置升级包的信息
     *
     * @param uploadInfoList 升级包的信息
     */
    public void setUploadInfoList(List<UploadInfo> uploadInfoList) {
        this.uploadInfoList = uploadInfoList;
    }


    @Override
    public String toString() {
        return "FotaAidlModelInfo{" +
                "modelID=" + modelID +
                ", modelName='" + modelName + '\'' +
                ", modelCurrentVersion=" + modelCurrentVersion +
                ", systemCurrentVersion=" + systemCurrentVersion +
                ", modelUpdateTime=" + modelUpdateTime +
                ", updateModelTaskInfoList=" + updateModelTaskInfoList +
                ", uploadInfoList=" + uploadInfoList +
                '}';
    }


    /**
     * 升级任务的信息
     */
    public static class UpdateModelTaskInfo implements Parcelable {
        private long id;//数据库自增id
        private int orderNum;//升级序号
        private int updateVersion;//更新的版本号
        private int systemMinVersion;//系统内核符合内核最小要求
        private int systemMaxVersion;//系统内核符合内核最大要求
        private long downloadFileSize;//文件大小
        private String downloadFileDownloadURL;//文件下载地址
        private String modelUpdateVersionDescription;//版本描述
        private String encryptionStr;//升级包加密串
        private int mustUpdate;//是否强制更新
        private String vehicleType;//车型
        private int modelID;//model的ID
        private String modelName;//model名称
        private String summary;//简介
        private int type;//更新类型 1.全量包,2.增量包
        private int startVersion;//开始版本
        private int targetVersion;//目标版本

        private List<UpdateRequirement> updateRequirementList;//升级必要条件

        private String showVersion;//显示版本号
        private String md5;
        private String secretKey;//升级包秘钥串

        private int packageType;//更新类型 1.全量包,2.增量包

        private int downloadState = -1;//下载状态 -1.无效状态，0.成功,1.失败，2.待下载
        private int verifyState = -1;//校验状态  -1.无效状态，0.成功,1.失败，2.待校验
        private int decodeState = -1;//解密状态 -1.无效状态， 0.成功,1.失败，2.待解密
        private int updateState = -1;//升级状态 -1.无效状态， 0.成功,1.失败，2.待升级，3.满足升级条件，4.不满足升级条件


        /**
         * 升级必要条件
         */
        public static class UpdateRequirement implements Parcelable {
            private long id;//自增ID
            private long taskId;//对应的任务ID
            private String taskModelName;//对应的任务Name
            private int modelId;
            private String modelName;
            private String requirement;//条件
            private int version;//版本号


            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public long getTaskId() {
                return taskId;
            }

            public void setTaskId(long taskId) {
                this.taskId = taskId;
            }

            public int getModelId() {
                return modelId;
            }

            public void setModelId(int modelId) {
                this.modelId = modelId;
            }

            /**
             * 获取升级条件的Model的ModelName
             * @return 升级条件的Model的ModelName
             */
            public String getModelName() {
                return modelName;
            }

            /**
             *设置升级条件的Model的ModelName
             * @param modelName 升级条件的Model的ModelName
             */
            public void setModelName(String modelName) {
                this.modelName = modelName;
            }

            /**
             * 获取对应的ModelName
             * @return ModelName
             */
            public String getTaskModelName() {
                return taskModelName;
            }

            /**
             * 设置对应的ModelName
             * @param taskModelName 对应的ModelName
             */
            public void setTaskModelName(String taskModelName) {
                this.taskModelName = taskModelName;
            }

            /**
             * 获取升级条件
             *
             * @return 升级条件
             */
            public String getRequirement() {
                return requirement;
            }

            /**
             * 设置升级条件
             *
             * @param requirement 升级条件
             */
            public void setRequirement(String requirement) {
                this.requirement = requirement;
            }

            /**
             * 获取升级依赖的版本号
             *
             * @return 升级依赖的版本号
             */
            public int getVersion() {
                return version;
            }

            /**
             * 设置升级依赖的版本号
             *
             * @param version 升级依赖的版本号
             */
            public void setVersion(int version) {
                this.version = version;
            }

            @Override
            public String toString() {
                return "UpdateRequirement{" +
                        "id=" + id +
                        ", taskId=" + taskId +
                        ", taskModelName=" + taskModelName +
                        ", modelId=" + modelId +
                        ", modelName='" + modelName + '\'' +
                        ", requirement='" + requirement + '\'' +
                        ", version=" + version +
                        '}';
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeLong(this.id);
                dest.writeLong(this.taskId);
                dest.writeString(this.taskModelName);
                dest.writeInt(this.modelId);
                dest.writeString(this.modelName);
                dest.writeString(this.requirement);
                dest.writeInt(this.version);
            }

            public UpdateRequirement() {
            }

            protected UpdateRequirement(Parcel in) {
                this.id = in.readLong();
                this.taskId = in.readLong();
                this.taskModelName = in.readString();
                this.modelId = in.readInt();
                this.modelName = in.readString();
                this.requirement = in.readString();
                this.version = in.readInt();
            }

            public static final Creator<UpdateRequirement> CREATOR = new Creator<UpdateRequirement>() {
                @Override
                public UpdateRequirement createFromParcel(Parcel source) {
                    return new UpdateRequirement(source);
                }

                @Override
                public UpdateRequirement[] newArray(int size) {
                    return new UpdateRequirement[size];
                }
            };
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        /**
         * 获取升级序号
         *
         * @return 升级序号
         */
        public int getOrderNum() {
            return orderNum;
        }

        /**
         * 设置升级序号
         *
         * @param orderNum 升级序号
         */
        public void setOrderNum(int orderNum) {
            this.orderNum = orderNum;
        }

        /**
         * 获取更新的版本号
         *
         * @return 更新的版本号
         */
        public int getUpdateVersion() {
            return updateVersion;
        }

        /**
         * 设置更新的版本号
         *
         * @param updateVersion 更新的版本号
         */
        public void setUpdateVersion(int updateVersion) {
            this.updateVersion = updateVersion;
        }

        /**
         * 获取系统内核符合内核最小要求
         *
         * @return 系统内核符合内核最小要求
         */
        public int getSystemMinVersion() {
            return systemMinVersion;
        }

        /**
         * 设置系统内核符合内核最小要求
         *
         * @param systemMinVersion 系统内核符合内核最小要求
         */
        public void setSystemMinVersion(int systemMinVersion) {
            this.systemMinVersion = systemMinVersion;
        }

        /**
         * 获取系统内核符合内核最大要求
         *
         * @return 系统内核符合内核最大要求
         */
        public int getSystemMaxVersion() {
            return systemMaxVersion;
        }

        /**
         * 设置系统内核符合内核最大要求
         *
         * @param systemMaxVersion 系统内核符合内核最大要求
         */
        public void setSystemMaxVersion(int systemMaxVersion) {
            this.systemMaxVersion = systemMaxVersion;
        }

        /**
         * 获取文件大小
         *
         * @return 文件大小
         */
        public long getDownloadFileSize() {
            return downloadFileSize;
        }

        /**
         * 设置文件大小
         *
         * @param downloadFileSize 文件大小
         */
        public void setDownloadFileSize(long downloadFileSize) {
            this.downloadFileSize = downloadFileSize;
        }

        /**
         * 获取文件下载地址
         *
         * @return 文件下载地址
         */
        public String getDownloadFileDownloadURL() {
            return downloadFileDownloadURL;
        }

        /**
         * 设置文件下载地址
         *
         * @param downloadFileDownloadURL 文件下载地址
         */
        public void setDownloadFileDownloadURL(String downloadFileDownloadURL) {
            this.downloadFileDownloadURL = downloadFileDownloadURL;
        }

        /**
         * 获取版本描述
         *
         * @return 版本描述
         */
        public String getModelUpdateVersionDescription() {
            return modelUpdateVersionDescription;
        }

        /**
         * 设置版本描述
         *
         * @param modelUpdateVersionDescription 版本描述
         */
        public void setModelUpdateVersionDescription(String modelUpdateVersionDescription) {
            this.modelUpdateVersionDescription = modelUpdateVersionDescription;
        }

        /**
         * 获取升级包加密串
         *
         * @return 升级包加密串
         */
        public String getEncryptionStr() {
            return encryptionStr;
        }

        /**
         * 设置升级包加密串
         *
         * @param encryptionStr 升级包加密串
         */
        public void setEncryptionStr(String encryptionStr) {
            this.encryptionStr = encryptionStr;
        }

        /**
         * 获取是否强制更新
         *
         * @return 0:需要更新，1:强制更新
         */
        public int getMustUpdate() {
            return mustUpdate;
        }

        /**
         * 设置是否强制更新
         *
         * @param mustUpdate 0:需要更新，1:强制更新
         */
        public void setMustUpdate(int mustUpdate) {
            this.mustUpdate = mustUpdate;
        }

        /**
         * 获取车型
         *
         * @return 车型
         */
        public String getVehicleType() {
            return vehicleType;
        }

        /**
         * 设置车型
         *
         * @param vehicleType 车型
         */
        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        /**
         * ModelID
         *
         * @return ModelID
         */
        public int getModelID() {
            return modelID;
        }

        /**
         * ModelID
         *
         * @param modelID ModelID
         */
        public void setModelID(int modelID) {
            this.modelID = modelID;
        }

        /**
         * 获取model名称
         *
         * @return model名称
         */
        public String getModelName() {
            return modelName;
        }

        /**
         * 设置model名称
         *
         * @param modelName model名称
         */
        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        /**
         * 获取简介
         *
         * @return 简介
         */
        public String getSummary() {
            return summary;
        }

        /**
         * 设置简介
         *
         * @param summary 简介
         */
        public void setSummary(String summary) {
            this.summary = summary;
        }

        /**
         * 获取更新类型
         *
         * @return 更新类型 1.全量包,2.增量包
         */
        public int getType() {
            return type;
        }

        /**
         * 设置更新类型
         *
         * @param type 更新类型 1.全量包,2.增量包
         */
        public void setType(int type) {
            this.type = type;
        }

        /**
         * 获取开始版本
         *
         * @return 开始版本
         */
        public int getStartVersion() {
            return startVersion;
        }

        /**
         * 设置开始版本
         *
         * @param startVersion 开始版本
         */
        public void setStartVersion(int startVersion) {
            this.startVersion = startVersion;
        }

        /**
         * 获取目标版本
         *
         * @return 目标版本
         */
        public int getTargetVersion() {
            return targetVersion;
        }

        /**
         * 设置目标版本
         *
         * @param targetVersion 目标版本
         */
        public void setTargetVersion(int targetVersion) {
            this.targetVersion = targetVersion;
        }

        /**
         * 获取升级必要条件
         *
         * @return 升级必要条件
         */
        public List<UpdateRequirement> getUpdateRequirementList() {
            return updateRequirementList;
        }

        /**
         * 设置升级必要条件
         *
         * @param updateRequirementList 升级必要条件
         */
        public void setUpdateRequirementList(List<UpdateRequirement> updateRequirementList) {
            this.updateRequirementList = updateRequirementList;
        }

        /**
         * 获得显示版本号
         *
         * @return 显示版本号
         */
        public String getShowVersion() {
            return showVersion;
        }

        /**
         * 设置显示版本号
         *
         * @param showVersion 显示版本号
         */
        public void setShowVersion(String showVersion) {
            this.showVersion = showVersion;
        }

        /**
         * 获取MD5值
         *
         * @return MD5值
         */
        public String getMd5() {
            return md5;
        }

        /**
         * 设置MD5值
         *
         * @param md5 MD5值
         */
        public void setMd5(String md5) {
            this.md5 = md5;
        }

        /**
         * 获取升级包秘钥串
         *
         * @return 升级包秘钥串
         */
        public String getSecretKey() {
            return secretKey;
        }

        /**
         * 设置升级包秘钥串
         *
         * @param secretKey 升级包秘钥串
         */
        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        /**
         * 获取 更新类型
         *
         * @return 更新类型 1.全量包,2.增量包
         */
        public int getPackageType() {
            return packageType;
        }

        /**
         * 设置更新类型
         *
         * @param packageType 更新类型 1.全量包,2.增量包
         */
        public void setPackageType(int packageType) {
            this.packageType = packageType;
        }


        /**
         * 获取下载状态
         *
         * @return 下载状态
         */
        public int getDownloadState() {
            return downloadState;
        }

        /**
         * 设置下载状态
         *
         * @param downloadState 下载状态
         */
        public void setDownloadState(int downloadState) {
            this.downloadState = downloadState;
        }

        /**
         * 获取校验状态
         *
         * @return 校验状态
         */
        public int getVerifyState() {
            return verifyState;
        }

        /**
         * 设置校验状态
         *
         * @param verifyState 校验状态
         */
        public void setVerifyState(int verifyState) {
            this.verifyState = verifyState;
        }

        /**
         * 获取解密状态
         *
         * @return 解密状态
         */
        public int getDecodeState() {
            return decodeState;
        }

        /**
         * 设置解密状态
         *
         * @param decodeState 解密状态
         */
        public void setDecodeState(int decodeState) {
            this.decodeState = decodeState;
        }

        /**
         * 获取升级状态
         *
         * @return 升级状态
         */
        public int getUpdateState() {
            return updateState;
        }

        /**
         * 设置升级状态
         *
         * @param updateState 升级状态
         */
        public void setUpdateState(int updateState) {
            this.updateState = updateState;
        }

        @Override
        public String toString() {
            return "UpdateModelTaskInfo{" +
                    "id=" + id +
                    ", orderNum=" + orderNum +
                    ", updateVersion=" + updateVersion +
                    ", systemMinVersion=" + systemMinVersion +
                    ", systemMaxVersion=" + systemMaxVersion +
                    ", downloadFileSize=" + downloadFileSize +
                    ", downloadFileDownloadURL='" + downloadFileDownloadURL + '\'' +
                    ", modelUpdateVersionDescription='" + modelUpdateVersionDescription + '\'' +
                    ", encryptionStr='" + encryptionStr + '\'' +
                    ", mustUpdate=" + mustUpdate +
                    ", vehicleType='" + vehicleType + '\'' +
                    ", modelID=" + modelID +
                    ", modelName='" + modelName + '\'' +
                    ", summary='" + summary + '\'' +
                    ", type=" + type +
                    ", startVersion=" + startVersion +
                    ", targetVersion=" + targetVersion +
                    ", updateRequirementList=" + updateRequirementList +
                    ", showVersion='" + showVersion + '\'' +
                    ", md5='" + md5 + '\'' +
                    ", secretKey='" + secretKey + '\'' +
                    ", packageType=" + packageType +
                    ", downloadState=" + downloadState +
                    ", verifyState=" + verifyState +
                    ", decodeState=" + decodeState +
                    ", updateState=" + updateState +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.id);
            dest.writeInt(this.orderNum);
            dest.writeInt(this.updateVersion);
            dest.writeInt(this.systemMinVersion);
            dest.writeInt(this.systemMaxVersion);
            dest.writeLong(this.downloadFileSize);
            dest.writeString(this.downloadFileDownloadURL);
            dest.writeString(this.modelUpdateVersionDescription);
            dest.writeString(this.encryptionStr);
            dest.writeInt(this.mustUpdate);
            dest.writeString(this.vehicleType);
            dest.writeInt(this.modelID);
            dest.writeString(this.modelName);
            dest.writeString(this.summary);
            dest.writeInt(this.type);
            dest.writeInt(this.startVersion);
            dest.writeInt(this.targetVersion);
            dest.writeList(this.updateRequirementList);
            dest.writeString(this.showVersion);
            dest.writeString(this.md5);
            dest.writeString(this.secretKey);
            dest.writeInt(this.packageType);
            dest.writeInt(this.downloadState);
            dest.writeInt(this.verifyState);
            dest.writeInt(this.decodeState);
            dest.writeInt(this.updateState);
        }

        public UpdateModelTaskInfo() {
        }

        protected UpdateModelTaskInfo(Parcel in) {
            this.id = in.readLong();
            this.orderNum = in.readInt();
            this.updateVersion = in.readInt();
            this.systemMinVersion = in.readInt();
            this.systemMaxVersion = in.readInt();
            this.downloadFileSize = in.readLong();
            this.downloadFileDownloadURL = in.readString();
            this.modelUpdateVersionDescription = in.readString();
            this.encryptionStr = in.readString();
            this.mustUpdate = in.readInt();
            this.vehicleType = in.readString();
            this.modelID = in.readInt();
            this.modelName = in.readString();
            this.summary = in.readString();
            this.type = in.readInt();
            this.startVersion = in.readInt();
            this.targetVersion = in.readInt();
            this.updateRequirementList = new ArrayList<UpdateRequirement>();
            in.readList(this.updateRequirementList, UpdateRequirement.class.getClassLoader());
            this.showVersion = in.readString();
            this.md5 = in.readString();
            this.secretKey = in.readString();
            this.packageType = in.readInt();
            this.downloadState = in.readInt();
            this.verifyState = in.readInt();
            this.decodeState = in.readInt();
            this.updateState = in.readInt();
        }

        public static final Creator<UpdateModelTaskInfo> CREATOR = new Creator<UpdateModelTaskInfo>() {
            @Override
            public UpdateModelTaskInfo createFromParcel(Parcel source) {
                return new UpdateModelTaskInfo(source);
            }

            @Override
            public UpdateModelTaskInfo[] newArray(int size) {
                return new UpdateModelTaskInfo[size];
            }
        };
    }


    /**
     * SDK返回给集成方升级包的信息
     */
    public static class UploadInfo implements Parcelable {
        private String modelFilePath;//升级包存放位置
        private long modelFileSize;//文件大小
        private String modelUpdateVersionDescription;//版本描述
        private int updateVersion;//升级版本号
        private boolean isMustUpdate;//是否强制更新
        private int systemMinVersion;//系统内核符合内核最小要求
        private int systemMaxVersion;//系统内核符合内核最大要求
        private String summary;//简介
        private int orderNum;//升级序号
        private String modelName;
        private int downloadState;//下载状态
        private int notificationState;//通知状态

        public int getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(int orderNum) {
            this.orderNum = orderNum;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        /**
         * 获得升级包存放位置
         *
         * @return 升级包存放位置
         */
        public String getModelFilePath() {
            return modelFilePath;
        }

        /**
         * 设置升级包存放位置
         *
         * @param modelFilePath 升级包存放位置
         */
        public void setModelFilePath(String modelFilePath) {
            this.modelFilePath = modelFilePath;
        }

        /**
         * 获得升级包的大小
         *
         * @return 升级包的大小
         */
        public long getModelFileSize() {
            return modelFileSize;
        }

        /**
         * 设置升级包的大小
         *
         * @param modelFileSize 升级包的大小
         */
        public void setModelFileSize(long modelFileSize) {
            this.modelFileSize = modelFileSize;
        }


        /**
         * 获得版本描述
         *
         * @return 版本描述
         */
        public String getModelUpdateVersionDescription() {
            return modelUpdateVersionDescription;
        }

        /**
         * 设置版本描述
         *
         * @param modelUpdateVersionDescription 版本描述
         */
        public void setModelUpdateVersionDescription(String modelUpdateVersionDescription) {
            this.modelUpdateVersionDescription = modelUpdateVersionDescription;
        }

        /**
         * 是否是否强制更新
         *
         * @return true 强制更新，false 不强制更新
         */
        public boolean isMustUpdate() {
            return isMustUpdate;
        }

        public void setMustUpdate(boolean mustUpdate) {
            isMustUpdate = mustUpdate;
        }

        /**
         * 获取更新的版本号
         *
         * @return 更新的版本号
         */
        public int getUpdateVersion() {
            return updateVersion;
        }

        /**
         * 设置更新的版本号
         *
         * @param updateVersion 更新的版本号
         */
        public void setUpdateVersion(int updateVersion) {
            this.updateVersion = updateVersion;
        }

        /**
         * 获取系统内核符合内核最小要求
         *
         * @return 系统内核符合内核最小要求
         */
        public int getSystemMinVersion() {
            return systemMinVersion;
        }

        /**
         * 设置系统内核符合内核最小要求
         *
         * @param systemMinVersion 系统内核符合内核最小要求
         */
        public void setSystemMinVersion(int systemMinVersion) {
            this.systemMinVersion = systemMinVersion;
        }

        /**
         * 获取系统内核符合内核最大要求
         *
         * @return 系统内核符合内核最大要求
         */
        public int getSystemMaxVersion() {
            return systemMaxVersion;
        }

        /**
         * 设置系统内核符合内核最大要求
         *
         * @param systemMaxVersion 系统内核符合内核最大要求
         */
        public void setSystemMaxVersion(int systemMaxVersion) {
            this.systemMaxVersion = systemMaxVersion;
        }

        /**
         * 获取简介
         *
         * @return 简介
         */
        public String getSummary() {
            return summary;
        }

        /**
         * 设置简介
         *
         * @param summary 简介
         */
        public void setSummary(String summary) {
            this.summary = summary;
        }

        public int getDownloadState() {
            return downloadState;
        }

        public void setDownloadState(int downloadState) {
            this.downloadState = downloadState;
        }

        public int getNotificationState() {
            return notificationState;
        }

        public void setNotificationState(int notificationState) {
            this.notificationState = notificationState;
        }

        @Override
        public String toString() {
            return "UploadInfo{" +
                    "modelFilePath='" + modelFilePath + '\'' +
                    ", modelFileSize=" + modelFileSize +
                    ", modelUpdateVersionDescription='" + modelUpdateVersionDescription + '\'' +
                    ", updateVersion=" + updateVersion +
                    ", isMustUpdate=" + isMustUpdate +
                    ", systemMinVersion=" + systemMinVersion +
                    ", systemMaxVersion=" + systemMaxVersion +
                    ", summary='" + summary + '\'' +
                    ", orderNum=" + orderNum +
                    ", modelName='" + modelName + '\'' +
                    ", downloadState=" + downloadState +
                    ", notificationState=" + notificationState +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.modelFilePath);
            dest.writeLong(this.modelFileSize);
            dest.writeString(this.modelUpdateVersionDescription);
            dest.writeInt(this.updateVersion);
            dest.writeByte(this.isMustUpdate ? (byte) 1 : (byte) 0);
            dest.writeInt(this.systemMinVersion);
            dest.writeInt(this.systemMaxVersion);
            dest.writeString(this.summary);
            dest.writeInt(this.orderNum);
            dest.writeString(this.modelName);
            dest.writeInt(this.downloadState);
            dest.writeInt(this.notificationState);
        }

        public UploadInfo() {
        }

        protected UploadInfo(Parcel in) {
            this.modelFilePath = in.readString();
            this.modelFileSize = in.readLong();
            this.modelUpdateVersionDescription = in.readString();
            this.updateVersion = in.readInt();
            this.isMustUpdate = in.readByte() != 0;
            this.systemMinVersion = in.readInt();
            this.systemMaxVersion = in.readInt();
            this.summary = in.readString();
            this.orderNum = in.readInt();
            this.modelName = in.readString();
            this.downloadState = in.readInt();
            this.notificationState = in.readInt();
        }

        public static final Creator<UploadInfo> CREATOR = new Creator<UploadInfo>() {
            @Override
            public UploadInfo createFromParcel(Parcel source) {
                return new UploadInfo(source);
            }

            @Override
            public UploadInfo[] newArray(int size) {
                return new UploadInfo[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.modelID);
        dest.writeString(this.modelName);
        dest.writeInt(this.modelCurrentVersion);
        dest.writeInt(this.systemCurrentVersion);
        dest.writeLong(this.modelUpdateTime);
        dest.writeList(this.updateModelTaskInfoList);
        dest.writeList(this.uploadInfoList);
    }

    public FotaAidlModelInfo() {
    }

    protected FotaAidlModelInfo(Parcel in) {
        this.id = in.readLong();
        this.modelID = in.readInt();
        this.modelName = in.readString();
        this.modelCurrentVersion = in.readInt();
        this.systemCurrentVersion = in.readInt();
        this.modelUpdateTime = in.readLong();
        this.updateModelTaskInfoList = new ArrayList<UpdateModelTaskInfo>();
        in.readList(this.updateModelTaskInfoList, UpdateModelTaskInfo.class.getClassLoader());
        this.uploadInfoList = new ArrayList<UploadInfo>();
        in.readList(this.uploadInfoList, UploadInfo.class.getClassLoader());
    }

    public static final Creator<FotaAidlModelInfo> CREATOR = new Creator<FotaAidlModelInfo>() {
        @Override
        public FotaAidlModelInfo createFromParcel(Parcel source) {
            return new FotaAidlModelInfo(source);
        }

        @Override
        public FotaAidlModelInfo[] newArray(int size) {
            return new FotaAidlModelInfo[size];
        }
    };
}
