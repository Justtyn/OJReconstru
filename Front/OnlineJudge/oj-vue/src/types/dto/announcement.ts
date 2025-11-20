export interface AnnouncementVO {
  id: number;
  title: string;
  content: string;
  time: string;
  pinned: boolean;
  isActive: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface AnnouncementQuery {
  page?: number;
  size?: number;
  keyword?: string;
  pinnedOnly?: boolean;
}
