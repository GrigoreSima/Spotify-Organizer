package organizer.backend.domain.objects;

public record SavedTracksBody(String[] ids, Object[] timestamped_ids) {
}
