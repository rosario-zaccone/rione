import { ProfileCard } from "../components/profile/ProfileCard";
import { ProfileEditor } from "../components/profile/ProfileEditor";

export function ProfilePage({ locations, locationsError, neighborhoodLabel, profile, user }) {
  return (
    <div className="page-grid">
      <ProfileCard neighborhoodLabel={neighborhoodLabel} user={user} />
      <ProfileEditor
        error={profile.error}
        loading={profile.loading}
        locations={locations}
        locationsError={locationsError}
        success={profile.success}
        user={user}
        onSubmit={profile.updateProfile}
      />
    </div>
  );
}
