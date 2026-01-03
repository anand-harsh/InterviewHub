export default function SkillBadge({ name, category, onRemove, variant = 'default' }) {
  const variants = {
    default: 'bg-amber-100 text-amber-900 border-amber-200',
    match: 'bg-green-100 text-green-900 border-green-200',
    selected: 'bg-orange-100 text-orange-900 border-orange-200'
  };

  return (
    <span className={`inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-sm font-medium border ${variants[variant]}`}>
      {name}
      {category && <span className="text-xs opacity-70">({category})</span>}
      {onRemove && (
        <button
          onClick={onRemove}
          className="hover:bg-red-200 rounded-full p-0.5 transition"
        >
          <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      )}
    </span>
  );
}