import React, { useEffect } from 'react';
import { CheckCircle, XCircle, Info, X } from 'lucide-react';

interface NotificationProps {
  id: string;
  message: string;
  type: 'success' | 'error' | 'info';
  onRemove: (id: string) => void;
}

const Notification: React.FC<NotificationProps> = ({ id, message, type, onRemove }) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onRemove(id);
    }, 5000);

    return () => clearTimeout(timer);
  }, [id, onRemove]);

  const icons = {
    success: CheckCircle,
    error: XCircle,
    info: Info,
  };

  const colors = {
    success: 'bg-emerald-900 border-emerald-600 text-emerald-100',
    error: 'bg-red-900 border-red-600 text-red-100',
    info: 'bg-blue-900 border-blue-600 text-blue-100',
  };

  const Icon = icons[type];

  return (
    <div className={`${colors[type]} border px-4 py-3 rounded-md shadow-lg mb-2 flex items-center space-x-3`}>
      <Icon className="w-5 h-5 flex-shrink-0" />
      <p className="text-sm flex-grow">{message}</p>
      <button
        onClick={() => onRemove(id)}
        className="text-white hover:text-gray-200 transition-colors"
      >
        <X className="w-4 h-4" />
      </button>
    </div>
  );
};

interface NotificationContainerProps {
  notifications: Array<{
    id: string;
    message: string;
    type: 'success' | 'error' | 'info';
  }>;
  onRemove: (id: string) => void;
}

export const NotificationContainer: React.FC<NotificationContainerProps> = ({
  notifications,
  onRemove,
}) => {
  if (notifications.length === 0) return null;

  return (
    <div className="fixed top-4 right-4 z-50 max-w-sm w-full">
      {notifications.map((notification) => (
        <Notification
          key={notification.id}
          {...notification}
          onRemove={onRemove}
        />
      ))}
    </div>
  );
};

export default Notification;