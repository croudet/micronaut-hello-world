package hello.world;

import java.time.OffsetDateTime;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(description = "Represents an Asset.", requiredProperties = {"id", "src"})
public class Asset {
    @Nullable
    private long assetId = -1L; // generated
    private long id = -1L; // given
    @Nullable
    private long mediaId = -1L;
    @Nullable
    private long size = -1L;
    @Nullable
    private float dur; // 0F
    private String src;
    @Nullable
    private String repositoryId = "";
    @Nullable
    private String type = "";
    @Nullable
    private String title = "";
    @Nullable
    private String md5 = "";
    @Nullable
    private OffsetDateTime expireDate;
    @Nullable
    private Boolean missing;

    public Asset() {
        // default
    }

    // id, mediaId, size, src, type, title, md5, repoId, dur
    public Asset(long assetId, long id, long mediaId, long size, String src, String type, String title, String md5, String repoId, float dur) {
        this(assetId, id, mediaId, size, src, type, title, md5, repoId, dur, null, null);
    }

    public Asset(long assetId, long id, long mediaId, long size, String src, String type, String title, String md5, String repoId, float dur, OffsetDateTime expireDate, Boolean missing) {
        this.assetId = assetId;
        this.id = id;
        this.mediaId = mediaId;
        this.size = size;
        this.src = src;
        this.repositoryId = repoId;
        this.type = type;
        this.title = title;
        this.md5 = md5;
        this.dur= dur;
        this.expireDate = expireDate;
        this.missing = missing;
    }

    @Schema(description = "The id of the Asset. Assigned by the DiffServer. Not required when inserting assets.", accessMode = AccessMode.READ_ONLY, nullable = true)
    public long getAssetId() {
        return assetId;
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
    }

    @Schema(description = "The asset Id. Given.", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Schema(description = "The mediaId of the Asset.", nullable = true)
    public long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    @Schema(description = "The src of the Asset.", nullable = false)
    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @Schema(description = "The type of the Asset.", nullable = true)
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Schema(description = "The title of the Asset.", nullable = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Schema(description = "The duration of the Asset.", nullable = true)
    public float getDur() {
        return dur;
    }

    public void setDur(float dur) {
        this.dur = dur;
    }

    @Schema(description = "The repository id of the Asset.", nullable = true)
    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repoId) {
        this.repositoryId = repoId;
    }

    @Schema(description = "The size of the Asset.", nullable = true)
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Schema(description = "The md5 of the Asset.", nullable = true)
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Schema(description = "The expiration date of the asset for a particular device or channel update.", nullable = true)
    public OffsetDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(OffsetDateTime expireDate) {
        this.expireDate = expireDate;
    }

    @Schema(description = "Tells whether this asset is missing on the device.", nullable = true)
    public Boolean getMissing() {
        return missing;
    }

    public void setMissing(Boolean missing) {
        this.missing = missing;
    }

    public boolean validate() {
        if (src == null) {
            return false;
        }
        // to make the unique constraint and then upsert work
        // https://www.postgresql.org/docs/11/indexes-unique.html
        // "Null values are not considered equal"

        // assetId, mediaId, size, src, title, md5, type, repoId, dur
        if (title == null) {
            title = "";
        }
        if (md5 == null) {
            md5 = "";
        }
        if (type == null) {
            type = "";
        }
        if (repositoryId == null) {
            repositoryId = "";
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder(300).append("Asset [id=").append(id).append(", assetId=").append(assetId).append(", mediaId=")
                .append(mediaId).append(", size=").append(size).append(", src=").append(src).append(", repoId=")
                .append(repositoryId).append(", type=").append(type).append(", title=").append(title).append(", md5=")
                .append(md5).append(", dur=").append(dur).append(", expireDate=").append(expireDate).append(", missing=").append(missing).append(']').toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(dur);
        result = prime * result + ((expireDate == null) ? 0 : expireDate.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
        result = prime * result + (int) (mediaId ^ (mediaId >>> 32));
        result = prime * result + ((repositoryId == null) ? 0 : repositoryId.hashCode());
        result = prime * result + (int) (size ^ (size >>> 32));
        result = prime * result + ((src == null) ? 0 : src.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((missing == null) ? 0 : missing.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Asset other = (Asset) obj;
        if (expireDate == null) {
            if (other.expireDate != null) {
                return false;
            }
        } else if (!expireDate.equals(other.expireDate)) {
            return false;
        }
        return equalsIgnoreExpireDate(other);
    }

    public boolean equalsIgnoreExpireDate(Asset other) {
        if (other == null) {
            return false;
        }
        if (Float.floatToIntBits(dur) != Float.floatToIntBits(other.dur)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (md5 == null) {
            if (other.md5 != null) {
                return false;
            }
        } else if (!md5.equals(other.md5)) {
            return false;
        }
        if (mediaId != other.mediaId) {
            return false;
        }
        if (repositoryId == null) {
            if (other.repositoryId != null) {
                return false;
            }
        } else if (!repositoryId.equals(other.repositoryId)) {
            return false;
        }
        if (size != other.size) {
            return false;
        }
        if (src == null) {
            if (other.src != null) {
                return false;
            }
        } else if (!src.equals(other.src)) {
            return false;
        }
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }
}
